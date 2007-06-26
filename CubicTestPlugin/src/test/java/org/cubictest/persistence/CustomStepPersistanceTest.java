package org.cubictest.persistence;

import static org.junit.Assert.*;

import org.cubictest.model.customstep.CustomTestStep;
import org.cubictest.model.customstep.data.CustomTestStepData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CustomStepPersistanceTest {

	private CustomTestStep customStep;
	private CubicTestXStream xstream;
	private String description = "My CustomStep description";
	private CustomTestStepData data;
	private String displayText = "displayText";
	private String path = "myPath";

	@Before
	public void setUp() throws Exception {
		customStep = new CustomTestStep();
		customStep.setDescription(description );
		data = customStep.getData("myDataKey"); 
		data.setDisplayText(displayText);
		data.setPath(path );
		xstream = new CubicTestXStream();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testObjectSerialization() {
		String xml = xstream.toXML(customStep);
		assertNotNull(xml);
		assertTrue(xml.trim().length() > 0 );
		assertTrue(xstream.fromXML(xml) instanceof CustomTestStep);
		assertNotNull(data);
	}

}
