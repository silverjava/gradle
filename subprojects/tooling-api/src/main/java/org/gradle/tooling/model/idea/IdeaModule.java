/*
 * Copyright 2011 the original author or authors.
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

package org.gradle.tooling.model.idea;

import org.gradle.tooling.model.BuildableElement;
import org.gradle.tooling.model.HierarchicalElement;

import java.io.File;
import java.util.List;

/**
 * Represents information about the IntelliJ IDEA module
 */
public interface IdeaModule extends BuildableElement, HierarchicalElement {

    /**
     * All content roots. Most idea modules have a single content root.
     *
     * @return content roots
     */
    List<File> getContentRoots();

    /**
     * Returns the project of this module.
     *
     * @return idea project
     */
    IdeaProject getParent();
}