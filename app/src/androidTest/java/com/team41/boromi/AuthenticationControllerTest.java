package com.team41.boromi;

import static com.team41.boromi.constants.CommonConstants.DB_TIMEOUT;
import static com.team41.boromi.dagger.BoromiModule.user;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;
import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team41.boromi.callbacks.AuthCallback;
import com.team41.boromi.controllers.AuthenticationController;
import com.team41.boromi.dbs.UserDB;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AuthenticationControllerTest {

	private final static String TAG = "AUTHENTICATION_CONTROLLER_TEST";

	ArrayList<Pair<String, String>> testUsers =  new ArrayList<>();

	FirebaseAuth auth = FirebaseAuth.getInstance();
	UserDB userDB =  new UserDB(FirebaseFirestore.getInstance());
	FirebaseFirestore db = FirebaseFirestore.getInstance();
	ExecutorService executor = new ThreadPoolExecutor(
			1,
			4,
			DB_TIMEOUT,
			TimeUnit.MILLISECONDS,
			new LinkedBlockingDeque<Runnable>()
	);

	AuthenticationController authController;

	@Before
	public void setup() throws InterruptedException {
		authController = new AuthenticationController(userDB, auth, executor);

		final Pair<String, String> emailAndPassword = new Pair<>(
				"testauthcontroller1@brockchelle.com",
				"testuser"
		);

		authController.makeSignUpRequest(
				"testauthcontroller1",
				emailAndPassword.first,
				emailAndPassword.second,
				new AuthCallback() {
					@Override
					public void onSuccess(AuthResult authResult) {
						testUsers.add(emailAndPassword);
					}

					@Override
					public void onFailure(Exception exception) {
						Log.e(TAG, "Sign up failure");
					}
				}
		);

		Thread.sleep(DB_TIMEOUT);
	}

	@After
	public void tearDown() throws InterruptedException {
		// Loop through all the added users
		for (Pair<String, String> emailAndPassword : testUsers) {
			// Login first (firebase won't let you delete without logging in first)
			authController.makeLoginRequest(
					emailAndPassword.first,
					emailAndPassword.second,
					new AuthCallback() {
						@Override
						public void onSuccess(AuthResult authResult) {
							Log.d(TAG, "Log in success");
						}

						@Override
						public void onFailure(Exception exception) {
							Log.e(TAG, "Log in failure");
						}
					});

			Thread.sleep(DB_TIMEOUT);

			// Deletes the user and their db entry
			auth.getCurrentUser().delete()
					.addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if (task.isSuccessful()) {
								Log.d(TAG, "Account deleted");

								try {
									Tasks.await(
											db.collection("users")
													.document(user.getUUID()).delete(),
											DB_TIMEOUT,
											TimeUnit.MILLISECONDS
									);
								} catch (Exception e) {
									Log.w(TAG, e.getCause());
								}
							}
						}
					});

			Thread.sleep(DB_TIMEOUT);
		}
	}

	@Test
	public void testSignUp() throws InterruptedException {
		// A valid sign up has already been tested in the setup so this will only test invalid
		Pair<String, String> emailAndPassword = testUsers.get(0);

		// Should fail because the email is already in use
		authController.makeSignUpRequest(
				"different_username",
				emailAndPassword.first,
				emailAndPassword.second
				,
				new AuthCallback() {
					@Override
					public void onSuccess(AuthResult authResult) {
						fail();
					}

					@Override
					public void onFailure(Exception exception) {
						assertTrue(true);
					}
				}
		);

		Thread.sleep(DB_TIMEOUT);

		// Should fail because the username is already in use
		authController.makeSignUpRequest(
				"testauthcontroller1",
				"differentemail@brockchelle.com",
				emailAndPassword.second
				,
				new AuthCallback() {
					@Override
					public void onSuccess(AuthResult authResult) {
						fail();
					}

					@Override
					public void onFailure(Exception exception) {
						assertTrue(true);
					}
				}
		);

		Thread.sleep(DB_TIMEOUT);

		// Should fail because the password is too weak
		authController.makeSignUpRequest(
				"newuser",
				"differentemail@brockchelle.com",
				"weak"
				,
				new AuthCallback() {
					@Override
					public void onSuccess(AuthResult authResult) {
						fail();
					}

					@Override
					public void onFailure(Exception exception) {
						assertTrue(true);
					}
				}
		);

		Thread.sleep(DB_TIMEOUT);
	}

	@Test
	public void testLogin() throws InterruptedException {
		Pair<String, String> emailAndPassword = testUsers.get(0);

		// Attempts an invalid login
		authController.makeLoginRequest(
				emailAndPassword.first,
				"nottheirpassword",
				new AuthCallback() {
					@Override
					public void onSuccess(AuthResult authResult) {
						fail();
					}

					@Override
					public void onFailure(Exception exception) {
						assertTrue(true);
					}
				}
		);

		Thread.sleep(DB_TIMEOUT);

		// Attempts a valid login
		authController.makeLoginRequest(
				emailAndPassword.first,
				emailAndPassword.second,
				new AuthCallback() {
					@Override
					public void onSuccess(AuthResult authResult) {
						assertTrue(true);
					}

					@Override
					public void onFailure(Exception exception) {
						fail();
					}
				}
		);

		Thread.sleep(DB_TIMEOUT);
	}

	@Test
	public void testChangeEmail() throws InterruptedException {
		Pair<String, String> emailAndPassword = testUsers.get(0);

		authController.changeEmail("testauthcontroller2@brockchelle.com");
		Thread.sleep(DB_TIMEOUT);

		assertEquals("testauthcontroller2@brockchelle.com", auth.getCurrentUser().getEmail());

		Pair<String, String> updatedEmailAndPassword = new Pair<>(
				"testauthcontroller2@brockchelle.com",
				emailAndPassword.second
		);
		testUsers.set(0, updatedEmailAndPassword);
	}

	@Test
	public void testFullFunctionality() throws InterruptedException {
		Pair<String, String> emailAndPassword = testUsers.get(0);

		// Attempts a valid login
		authController.makeLoginRequest(
				emailAndPassword.first,
				emailAndPassword.second,
				new AuthCallback() {
					@Override
					public void onSuccess(AuthResult authResult) {
						assertTrue(true);
					}

					@Override
					public void onFailure(Exception exception) {
						fail();
					}
				}
		);

		Thread.sleep(DB_TIMEOUT);

		// Changes the email of the authenticated  user
		authController.changeEmail("testauthcontroller2@brockchelle.com");

		Thread.sleep(DB_TIMEOUT);

		assertEquals("testauthcontroller2@brockchelle.com", auth.getCurrentUser().getEmail());
		Pair<String, String> updatedEmailAndPassword = new Pair<>(
				"testauthcontroller2@brockchelle.com",
				emailAndPassword.second
		);
		testUsers.set(0, updatedEmailAndPassword);

		// Attempts to login with the new email
		authController.makeLoginRequest(
				updatedEmailAndPassword.first,
				updatedEmailAndPassword.second,
				new AuthCallback() {
					@Override
					public void onSuccess(AuthResult authResult) {
						assertTrue(true);
					}

					@Override
					public void onFailure(Exception exception) {
						fail();
					}
				}
		);

		Thread.sleep(DB_TIMEOUT);
	}

}
