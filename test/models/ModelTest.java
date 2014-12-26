package models;

import org.junit.*;

import play.test.*;
import static play.test.Helpers.*;

public abstract class ModelTest {
	protected static FakeApplication fakeApplication;
	
	// TODO: change before & after helper: only setup database
  @Before
  public void startFakeApplication() {
  	fakeApplication = fakeApplication();
  }
  
  @After
  public void shutdownApplication() {
  	stop(fakeApplication);
  }
}
