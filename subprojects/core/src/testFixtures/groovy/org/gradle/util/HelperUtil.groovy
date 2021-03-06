/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.util

import org.apache.ivy.core.module.descriptor.Configuration
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor
import org.apache.ivy.core.module.descriptor.DefaultExcludeRule
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor
import org.apache.ivy.core.module.id.ArtifactId
import org.apache.ivy.core.module.id.ModuleId
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.plugins.matcher.ExactPatternMatcher
import org.apache.ivy.plugins.matcher.PatternMatcher
import org.codehaus.groovy.control.CompilerConfiguration
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.internal.artifacts.publish.DefaultPublishArtifact
import org.gradle.api.internal.project.DefaultProject
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.project.taskfactory.ITaskFactory
import org.gradle.groovy.scripts.DefaultScript
import org.gradle.groovy.scripts.Script
import org.gradle.groovy.scripts.ScriptSource
import org.gradle.test.fixtures.file.TestNameTestDirectoryProvider
import org.gradle.testfixtures.ProjectBuilder

import java.rmi.server.UID

/**
 * @author Hans Dockter
 */
class HelperUtil {
     public static final Closure TEST_CLOSURE = {}

     static <T extends Task> T createTask(Class<T> type) {
         return createTask(type, createRootProject())
     }

     static <T extends Task> T createTask(Class<T> type, Map taskFields) {
         def task = createTask(type, createRootProject())
         hackInTaskProperties(type, task, taskFields)
         return task
     }

    private static void hackInTaskProperties(Class type, Task task, Map args) {
        args.each { k, v ->
            def field = type.getDeclaredField(k)
            field.setAccessible(true)
            field.set(task, v)
        }
    }

    static <T extends Task> T createTask(Class<T> type, ProjectInternal project) {
         return createTask(type, project, 'name')
     }

     static <T extends Task> T createTask(Class<T> type, ProjectInternal project, String name) {
         return project.services.get(ITaskFactory).createTask([name: name, type: type])
     }

    static ProjectBuilder builder() {
        return ProjectBuilder.builder().withProjectDir(TestNameTestDirectoryProvider.newInstance().testDirectory)
    }

     static DefaultProject createRootProject() {
         createRootProject(TestNameTestDirectoryProvider.newInstance().testDirectory)
     }

     static DefaultProject createRootProject(File rootDir) {
         return ProjectBuilder
                 .builder()
                 .withProjectDir(rootDir)
                 .build()
     }

     static DefaultProject createChildProject(DefaultProject parent, String name, File projectDir = null) {
         return ProjectBuilder
                 .builder()
                 .withName(name)
                 .withParent(parent)
                 .withProjectDir(projectDir)
                 .build();
     }

     static DefaultExcludeRule getTestExcludeRule(def module = 'module') {
         new DefaultExcludeRule(new ArtifactId(
                 new ModuleId('org', module), PatternMatcher.ANY_EXPRESSION,
                 PatternMatcher.ANY_EXPRESSION,
                 PatternMatcher.ANY_EXPRESSION),
                 ExactPatternMatcher.INSTANCE, null)
     }

     static DefaultDependencyDescriptor getTestDescriptor() {
         new DefaultDependencyDescriptor(ModuleRevisionId.newInstance('org', 'name', 'rev'), false)
     }

     static DefaultModuleDescriptor createModuleDescriptor(Set confs) {
         DefaultModuleDescriptor moduleDescriptor = new DefaultModuleDescriptor(ModuleRevisionId.newInstance('org', 'name', 'rev'), "status", null)
         confs.each { moduleDescriptor.addConfiguration(new Configuration(it)) }
         return moduleDescriptor;
     }

     static BuildResult createBuildResult(Throwable t) {
         return new BuildResult(null, t);
     }

     static ModuleDependency createDependency(String group, String name, String version) {
       new DefaultExternalModuleDependency(group, name, version)
     }

     static DefaultPublishArtifact createPublishArtifact(String name, String extension, String type, String classifier) {
       new DefaultPublishArtifact(name, extension, type, classifier, new Date(), new File(""))
     }

     static groovy.lang.Script createScript(String code) {
         new GroovyShell().parse(code)
     }

     static Object call(String text, Object... params) {
         toClosure(text).call(*params)
     }

     static Closure toClosure(String text) {
         return new GroovyShell().evaluate("return " + text)
     }

     static Closure toClosure(ScriptSource source) {
         CompilerConfiguration configuration = new CompilerConfiguration();
         configuration.setScriptBaseClass(TestScript.getName());

         GroovyShell shell = new GroovyShell(configuration)
         Script script = shell.parse(source.resource.text)
         script.setScriptSource(source)
         return script.run()
     }

     static Closure toClosure(TestClosure closure) {
         return { param -> closure.call(param) }
     }

     static Closure returns(Object value) {
         return { value }
     }

     static Closure createSetterClosure(String name, String value) {
         return {
             "set$name"(value)
         }
     }

     static String createUniqueId() {
         return new UID().toString();
     }
 }


interface TestClosure {
    Object call(Object param);
}

abstract class TestScript extends DefaultScript {
}
