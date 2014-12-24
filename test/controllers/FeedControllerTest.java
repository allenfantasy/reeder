package controllers;

import org.junit.Test;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class FeedControllerTest {

  @Test
  public void callIndex() {
  	running(fakeApplication(), new Runnable() {
			
			@Override
			public void run() {
				Result result = callAction(
			  	controllers.routes.ref.FeedsController.index()
			  );
			  assertThat(status(result)).isEqualTo(OK);
			}
		});
  }
}
