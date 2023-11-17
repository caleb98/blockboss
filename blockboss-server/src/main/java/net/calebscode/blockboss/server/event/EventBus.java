package net.calebscode.blockboss.server.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.calebscode.blockboss.logging.Logging;

public class EventBus implements Logging {
	
	private Map<Class<?>, List<SubscriberMethod>> subscriptions;
	
	public EventBus() {
		subscriptions = new HashMap<>();
	}
	
	public void send(Object data) {
		Class<?> dataClass = data.getClass();
		if (subscriptions.containsKey(dataClass)) {
			subscriptions.get(dataClass).forEach(subscription -> subscription.call(data));
		}
	}

	public void register(Object subscriber) {
		Class<?> subscriberClass = subscriber.getClass();
		
		while (subscriberClass != null) {
			List<Method> methods = findSubscribeAnnotatedMethods(subscriberClass);
			
			for (var method : methods) {
				Class<?> dataClass = method.getParameterTypes()[0];
				SubscriberMethod subMethod = new SubscriberMethod(subscriber, method);
				
				if (!subscriptions.containsKey(dataClass)) {
					subscriptions.put(dataClass, new ArrayList<>());
				}
				
				subscriptions.get(dataClass).add(subMethod);
			}
			
			subscriberClass = subscriberClass.getSuperclass();
		}
	}
	
	private List<Method> findSubscribeAnnotatedMethods(Class<?> clazz) {
		return Arrays.stream(clazz.getDeclaredMethods())
			.filter(method -> method.isAnnotationPresent(Subscribe.class))
			.filter(method -> methodHasOneArgument(clazz, method))
			.toList();
	}

	private boolean methodHasOneArgument(Class<?> clazz, Method method) {
		if (method.getParameterCount() > 1) {
			logger().error(
				"@Subscribe annotated method {}.{} has too many arguments.",
				clazz.getSimpleName(),
				method.getName()
			);
			return false;
		}
		return true;
	}
	
	private class SubscriberMethod {
		
		Object callee;
		Method method;
		
		SubscriberMethod(Object callee, Method method) {
			this.callee = callee;
			this.method = method;
		}
		
		void call(Object parameter) {
			try {
				method.invoke(callee, parameter);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger().error(
					"Error calling subscriber method {}.{}: {}",
					callee.getClass().getSimpleName(),
					method.getName(),
					e.getMessage()
				);
			}
		}
		
	}
	
}
