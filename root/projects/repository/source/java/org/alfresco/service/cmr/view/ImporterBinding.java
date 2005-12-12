/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.service.cmr.view;


/**
 * Encapsulation of Import binding parameters
 * 
 * @author David Caruana
 */
public interface ImporterBinding
{

    /**
     * UUID Binding 
     */
    public enum UUID_BINDING
    {
        CREATE_NEW, REMOVE_EXISTING, REPLACE_EXISTING, THROW_ON_COLLISION
    }

    /**
     * Gets the Node UUID Binding
     * 
     * @return  UUID_BINDING
     */
    public UUID_BINDING getUUIDBinding();

    /**
     * Gets a value for the specified name - to support simple name / value binding
     * 
     * @param key   the value name
     * @return  the value
     */
    public String getValue(String key);

}
