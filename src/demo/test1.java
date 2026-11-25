package demo;

import static io.restassured.RestAssured.given;

import io.restassured.path.json.JsonPath;

public class test1 {

	public static void main(String[] args) {
		// Step 1: get an access token.
		// The old version logged in to Google through Selenium (authorization-code grant).
		// Google now blocks automated logins, so we use the client-credentials grant instead.
		String response = given()
				.formParam("client_id", "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
				.formParam("client_secret", "erZOWM9g3UtwNRj340YYaK_W")
				.formParam("grant_type", "client_credentials")
				.formParam("scope", "trust")
				.when().log().all()
				.post("https://rahulshettyacademy.com/oauthapi/oauth2/resourceOwner/token")
				.asString();

		JsonPath jsonPath = new JsonPath(response);
		String accessToken = jsonPath.getString("access_token");
		System.out.println(accessToken);

		// Step 2: call the protected API with the token and print the raw response
		String r2 = given()
				.queryParam("access_token", accessToken)
				.when()
				.get("https://rahulshettyacademy.com/oauthapi/getCourseDetails")
				.asString();
		System.out.println(r2);
	}

}
