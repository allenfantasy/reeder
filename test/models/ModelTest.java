package models;

import org.junit.*;

import play.test.*;
import static play.test.Helpers.*;

public abstract class ModelTest {
	protected static FakeApplication app;
	
	// TODO: change before & after helper: only setup database
  @Before
  public void startFakeApplication() {
  	app = fakeApplication(inMemoryDatabase());
  	start(app);
  }
  
  @After
  public void shutdownApplication() {
  	stop(app);
  }
}
