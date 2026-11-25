package demo;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;

import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import pojo.Api;
import pojo.GetCourse;

public class oAuthTest {

	public static void main(String[] args) {
		String[] courseTitles = { "Selenium Webdriver Java", "Cypress", "Protractor" };

		// Step 1: get an access token.
		// The old version logged in to Google through Selenium (authorization-code grant).
		// Google now blocks automated logins, so we use the client-credentials grant instead.
		String tokenResponse = given()
				.formParam("client_id", "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
				.formParam("client_secret", "erZOWM9g3UtwNRj340YYaK_W")
				.formParam("grant_type", "client_credentials")
				.formParam("scope", "trust")
				.when().log().all()
				.post("https://rahulshettyacademy.com/oauthapi/oauth2/resourceOwner/token")
				.asString();
		System.out.println(tokenResponse);

		JsonPath js = new JsonPath(tokenResponse);
		String accessToken = js.getString("access_token");

		// Step 2: call the protected API and DESERIALIZE the JSON into the GetCourse POJO
		GetCourse gc = given().queryParam("access_token", accessToken)
				.expect().defaultParser(Parser.JSON)
				.when()
				.get("https://rahulshettyacademy.com/oauthapi/getCourseDetails")
				.as(GetCourse.class);

		System.out.println(gc.getLinkedIn());
		System.out.println(gc.getInstructor());
		System.out.println(gc.getCourses().getApi().get(1).getCourseTitle());

		// Print the price of "SoapUI Webservices testing"
		List<Api> apiCourses = gc.getCourses().getApi();
		for (int i = 0; i < apiCourses.size(); i++) {
			if (apiCourses.get(i).getCourseTitle().equalsIgnoreCase("SoapUI Webservices testing")) {
				System.out.println(apiCourses.get(i).getPrice());
			}
		}

		// Get the course names of WebAutomation and compare with expected list
		ArrayList<String> a = new ArrayList<String>();
		List<pojo.WebAutomation> w = gc.getCourses().getWebAutomation();
		for (int j = 0; j < w.size(); j++) {
			a.add(w.get(j).getCourseTitle());
		}

		List<String> expectedList = Arrays.asList(courseTitles);
		Assert.assertTrue(a.equals(expectedList));
		System.out.println("WebAutomation courses match: " + a);
	}

}
