/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.repo.dictionary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.alfresco.i18n.I18NUtil;
import org.alfresco.repo.dictionary.constraint.RegexConstraint;
import org.alfresco.repo.dictionary.constraint.StringLengthConstraint;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;


public class DictionaryDAOTest extends TestCase
{
    public static final String TEST_RESOURCE_MESSAGES = "alfresco/messages/dictionary-messages";

    private static final String TEST_URL = "http://www.alfresco.org/test/dictionarydaotest/1.0";
    private static final String TEST_MODEL = "org/alfresco/repo/dictionary/dictionarydaotest_model.xml";
    private static final String TEST_BUNDLE = "org/alfresco/repo/dictionary/dictionarydaotest_model";
    private DictionaryService service;
    
    
    @Override
    public void setUp()
    {
        // register resource bundles for messages
        I18NUtil.registerResourceBundle(TEST_RESOURCE_MESSAGES);
        
        // Instantiate Dictionary Service
        NamespaceDAO namespaceDAO = new NamespaceDAOImpl();
        DictionaryDAOImpl dictionaryDAO = new DictionaryDAOImpl(namespaceDAO);

        // Populate with appropriate models
        DictionaryBootstrap bootstrap = new DictionaryBootstrap();
        List<String> bootstrapModels = new ArrayList<String>();
        bootstrapModels.add("alfresco/model/dictionaryModel.xml");
        bootstrapModels.add(TEST_MODEL);
        List<String> labels = new ArrayList<String>();
        labels.add(TEST_BUNDLE);
        bootstrap.setModels(bootstrapModels);
        bootstrap.setLabels(labels);
        bootstrap.setDictionaryDAO(dictionaryDAO);
        bootstrap.bootstrap();
        
        DictionaryComponent component = new DictionaryComponent();
        component.setDictionaryDAO(dictionaryDAO);
        service = component;
    }
    

    public void testBootstrap()
    {
        NamespaceDAO namespaceDAO = new NamespaceDAOImpl();
        DictionaryDAOImpl dictionaryDAO = new DictionaryDAOImpl(namespaceDAO);
        
        DictionaryBootstrap bootstrap = new DictionaryBootstrap();
        List<String> bootstrapModels = new ArrayList<String>();
        
        bootstrapModels.add("alfresco/model/dictionaryModel.xml");
        bootstrapModels.add("alfresco/model/systemModel.xml");
        bootstrapModels.add("alfresco/model/contentModel.xml");
        bootstrapModels.add("alfresco/model/wcmModel.xml");
        bootstrapModels.add("alfresco/model/applicationModel.xml");
        
        bootstrapModels.add("org/alfresco/repo/security/authentication/userModel.xml");
        bootstrapModels.add("org/alfresco/repo/action/actionModel.xml");
        bootstrapModels.add("org/alfresco/repo/rule/ruleModel.xml");
        bootstrapModels.add("org/alfresco/repo/version/version_model.xml");
        
        bootstrap.setModels(bootstrapModels);
        bootstrap.setDictionaryDAO(dictionaryDAO);
        bootstrap.bootstrap();        
    }


    public void testLabels()
    {
        QName model = QName.createQName(TEST_URL, "dictionarydaotest");
        ModelDefinition modelDef = service.getModel(model);
        assertEquals("Model Description", modelDef.getDescription());
        QName type = QName.createQName(TEST_URL, "base");
        TypeDefinition typeDef = service.getType(type);
        assertEquals("Base Title", typeDef.getTitle());
        assertEquals("Base Description", typeDef.getDescription());
        QName prop = QName.createQName(TEST_URL, "prop1");
        PropertyDefinition propDef = service.getProperty(prop);
        assertEquals("Prop1 Title", propDef.getTitle());
        assertEquals("Prop1 Description", propDef.getDescription());
        QName assoc = QName.createQName(TEST_URL, "assoc1");
        AssociationDefinition assocDef = service.getAssociation(assoc);
        assertEquals("Assoc1 Title", assocDef.getTitle());
        assertEquals("Assoc1 Description", assocDef.getDescription());
        QName datatype = QName.createQName(TEST_URL, "datatype");
        DataTypeDefinition datatypeDef = service.getDataType(datatype);
        assertEquals("Datatype Analyser", datatypeDef.getAnalyserClassName());
    }
    
    public void testConstraints()
    {
        // get the constraints for a property without constraints
        QName propNoConstraintsQName = QName.createQName(TEST_URL, "fileprop");
        PropertyDefinition propNoConstraintsDef = service.getProperty(propNoConstraintsQName);
        assertNotNull("Property without constraints returned null list", propNoConstraintsDef.getConstraints());
        
        // get the constraints defined for the property
        QName prop1QName = QName.createQName(TEST_URL, "prop1");
        PropertyDefinition propDef = service.getProperty(prop1QName);
        List<ConstraintDefinition> constraints = propDef.getConstraints();
        assertNotNull("Null constraints list", constraints);
        assertEquals("Incorrect number of constraints", 2, constraints.size());
        
        // check the individual constraints
        ConstraintDefinition constraintDef = constraints.get(0);
        assertTrue("Constraint anonymous name incorrect", constraintDef.getName().getLocalName().startsWith("prop1_anon"));
        // check that the constraint implementation is valid (it used a reference)
        Constraint constraint = constraintDef.getConstraint();
        assertNotNull("Reference constraint has no implementation", constraint);
        
        // make sure it is the correct type of constraint
        assertTrue("Expected type REGEX constraint", constraint instanceof RegexConstraint);
    }
    
    public void testConstraintsOverrideInheritance()
    {
        QName baseQName = QName.createQName(TEST_URL, "base");
        QName fileQName = QName.createQName(TEST_URL, "file");
        QName folderQName = QName.createQName(TEST_URL, "folder");
        QName prop1QName = QName.createQName(TEST_URL, "prop1");

        // get the base property
        PropertyDefinition prop1Def = service.getProperty(baseQName, prop1QName);
        assertNotNull(prop1Def);
        List<ConstraintDefinition> prop1Constraints = prop1Def.getConstraints();
        assertEquals("Incorrect number of constraints", 2, prop1Constraints.size());
        assertTrue("Constraint instance incorrect", prop1Constraints.get(0).getConstraint() instanceof RegexConstraint);
        assertTrue("Constraint instance incorrect", prop1Constraints.get(1).getConstraint() instanceof StringLengthConstraint);

        // check the inherited property on folder (must be same as above)
        prop1Def = service.getProperty(folderQName, prop1QName);
        assertNotNull(prop1Def);
        prop1Constraints = prop1Def.getConstraints();
        assertEquals("Incorrect number of constraints", 2, prop1Constraints.size());
        assertTrue("Constraint instance incorrect", prop1Constraints.get(0).getConstraint() instanceof RegexConstraint);
        assertTrue("Constraint instance incorrect", prop1Constraints.get(1).getConstraint() instanceof StringLengthConstraint);

        // check the overridden property on file (must be reverse of above)
        prop1Def = service.getProperty(fileQName, prop1QName);
        assertNotNull(prop1Def);
        prop1Constraints = prop1Def.getConstraints();
        assertEquals("Incorrect number of constraints", 2, prop1Constraints.size());
        assertTrue("Constraint instance incorrect", prop1Constraints.get(0).getConstraint() instanceof StringLengthConstraint);
        assertTrue("Constraint instance incorrect", prop1Constraints.get(1).getConstraint() instanceof RegexConstraint);
    }

    public void testArchive()
    {
        QName testFileQName = QName.createQName(TEST_URL, "file");
        ClassDefinition fileClassDef = service.getClass(testFileQName);
        assertTrue("File type should have the archive flag", fileClassDef.isArchive());

        QName testFileDerivedQName = QName.createQName(TEST_URL, "file-derived");
        ClassDefinition fileDerivedClassDef = service.getClass(testFileDerivedQName);
        assertTrue("Direct derived File type should have the archive flag", fileDerivedClassDef.isArchive());

        QName testFileDerivedNoArchiveQName = QName.createQName(TEST_URL, "file-derived-no-archive");
        ClassDefinition fileDerivedNoArchiveClassDef = service.getClass(testFileDerivedNoArchiveQName);
        assertFalse("Derived File with archive override type should NOT have the archive flag",
                fileDerivedNoArchiveClassDef.isArchive());

        QName testFolderQName = QName.createQName(TEST_URL, "folder");
        ClassDefinition folderClassDef = service.getClass(testFolderQName);
        assertFalse("Folder type should not have the archive flag", folderClassDef.isArchive());
    }
    
    public void testMandatoryEnforced()
    {
        // get the properties for the test type
        QName testEnforcedQName = QName.createQName(TEST_URL, "enforced");
        ClassDefinition testEnforcedClassDef = service.getClass(testEnforcedQName);
        Map<QName, PropertyDefinition> testEnforcedPropertyDefs = testEnforcedClassDef.getProperties();
        
        PropertyDefinition propertyDef = null;

        QName testMandatoryEnforcedQName = QName.createQName(TEST_URL, "mandatory-enforced");
        propertyDef = testEnforcedPropertyDefs.get(testMandatoryEnforcedQName);
        assertNotNull("Property not found: " + testMandatoryEnforcedQName,
                propertyDef);
        assertTrue("Expected property to be mandatory: " + testMandatoryEnforcedQName,
                propertyDef.isMandatory());
        assertTrue("Expected property to be mandatory-enforced: " + testMandatoryEnforcedQName,
                propertyDef.isMandatoryEnforced());

        QName testMandatoryNotEnforcedQName = QName.createQName(TEST_URL, "mandatory-not-enforced");
        propertyDef = testEnforcedPropertyDefs.get(testMandatoryNotEnforcedQName);
        assertNotNull("Property not found: " + testMandatoryNotEnforcedQName,
                propertyDef);
        assertTrue("Expected property to be mandatory: " + testMandatoryNotEnforcedQName,
                propertyDef.isMandatory());
        assertFalse("Expected property to be mandatory-not-enforced: " + testMandatoryNotEnforcedQName,
                propertyDef.isMandatoryEnforced());

        QName testMandatoryDefaultEnforcedQName = QName.createQName(TEST_URL, "mandatory-default-enforced");
        propertyDef = testEnforcedPropertyDefs.get(testMandatoryDefaultEnforcedQName);
        assertNotNull("Property not found: " + testMandatoryDefaultEnforcedQName,
                propertyDef);
        assertTrue("Expected property to be mandatory: " + testMandatoryDefaultEnforcedQName,
                propertyDef.isMandatory());
        assertFalse("Expected property to be mandatory-not-enforced: " + testMandatoryDefaultEnforcedQName,
                propertyDef.isMandatoryEnforced());
    }
    
    public void testSubClassOf()
    {
        QName invalid = QName.createQName(TEST_URL, "invalid");
        QName base = QName.createQName(TEST_URL, "base");
        QName file = QName.createQName(TEST_URL, "file");
        QName folder = QName.createQName(TEST_URL, "folder");
        QName referenceable = QName.createQName(TEST_URL, "referenceable");

        // Test invalid args
        try
        {
            service.isSubClass(invalid, referenceable);
            fail("Failed to catch invalid class parameter");
        }
        catch(InvalidTypeException e) {}

        try
        {
            service.isSubClass(referenceable, invalid);
            fail("Failed to catch invalid class parameter");
        }
        catch(InvalidTypeException e) {}

        // Test various flavours of subclassof
        boolean test1 = service.isSubClass(file, referenceable);  // type vs aspect
        assertFalse(test1);
        boolean test2 = service.isSubClass(file, folder);   // seperate hierarchies
        assertFalse(test2);
        boolean test3 = service.isSubClass(file, file);   // self
        assertTrue(test3);
        boolean test4 = service.isSubClass(folder, base);  // subclass
        assertTrue(test4);
        boolean test5 = service.isSubClass(base, folder);  // reversed test
        assertFalse(test5);
    }
    

    public void testPropertyOverride()
    {
        TypeDefinition type1 = service.getType(QName.createQName(TEST_URL, "overridetype1"));
        Map<QName, PropertyDefinition> props1 = type1.getProperties();
        PropertyDefinition prop1 = props1.get(QName.createQName(TEST_URL, "propoverride"));
        String def1 = prop1.getDefaultValue();
        assertEquals("one", def1);
        
        TypeDefinition type2 = service.getType(QName.createQName(TEST_URL, "overridetype2"));
        Map<QName, PropertyDefinition> props2 = type2.getProperties();
        PropertyDefinition prop2 = props2.get(QName.createQName(TEST_URL, "propoverride"));
        String def2 = prop2.getDefaultValue();
        assertEquals("two", def2);

        TypeDefinition type3 = service.getType(QName.createQName(TEST_URL, "overridetype3"));
        Map<QName, PropertyDefinition> props3 = type3.getProperties();
        PropertyDefinition prop3 = props3.get(QName.createQName(TEST_URL, "propoverride"));
        String def3 = prop3.getDefaultValue();
        assertEquals("three", def3);
    }

}
