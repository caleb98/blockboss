package net.calebscode.blockboss.gradle;

import java.util.Arrays
import java.util.Map

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Jar

public class BlockBossDistributionPlugin implements Plugin<Project> {

	void apply(Project project) {
		project.tasks.named('jar') {
			manifest {
				attributes 'Main-Class': project.application.mainClass
			}
		}

		project.distributions {
			main {
				contents {
					from('launcher') {
						expand project.properties
					}

					from('blockboss-config') {
						into 'blockboss-config'
					}
				}
			}
		}
	}

}