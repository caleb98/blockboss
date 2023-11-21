@echo off
pushd %~dp0
call .\\bin\\${project.name}.bat %*
popd