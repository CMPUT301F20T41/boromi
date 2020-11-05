package com.team41.boromi;

import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team41.boromi.dbs.UserDB;
import com.team41.boromi.models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.team41.boromi.constants.CommonConstants.DB_TIMEOUT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class UserDBTest {

  UserDB userDB;
  ArrayList<User> testUsers = new ArrayList<>();

  // We also need the db so we can perform deletions which is a non-required functionality
  // for the UserDB class within the actual application
  FirebaseFirestore db;

  @Before
  public void setup() {
    db = FirebaseFirestore.getInstance();
    userDB = new UserDB(db);

    User user = new User("0000000000", "testuser998", "testuser998@brockchelle.com");
    testUsers.add(userDB.pushUser(user));
  }

  @After
  public void tearDown() {
    // Since the userDB doesn't provide a delete method and I don't think it's necessary to
    // add one at the moment I just wrote it as a part of the test so that the db can be
    // cleaned up between tests
    for (User user : testUsers) {
      try {
        Tasks.await(
                db.collection("users").document(user.getUUID()).delete(),
                DB_TIMEOUT,
                TimeUnit.MILLISECONDS
        );
      } catch (Exception e) {
        Log.w("UserDBTest", e.getCause());
      }
    }
  }

  @Test
  public void testGetUserByUUIDNotFound() {
    User user = userDB.getUserByUUID("9999999999");
    assertNull(user);
  }

  @Test
  public void testGetUserByUUID() {
    User user = userDB.getUserByUUID("0000000000");

    assertEquals("0000000000", user.getUUID());
    assertEquals("testuser998", user.getUsername());
    assertEquals("testuser998@brockchelle.com", user.getEmail());
  }

  @Test
  public void testGetUserByUsernameNotFound() {
    User user = userDB.getUserByUsername("really_long_username_that_should_not_exist");
    assertNull(user);
  }

  @Test
  public void testGetUserByUsername() {
    User user = userDB.getUserByUsername("testuser998");

    assertEquals("0000000000", user.getUUID());
    assertEquals("testuser998", user.getUsername());
    assertEquals("testuser998@brockchelle.com", user.getEmail());
  }

  @Test
  public void testCreateUser() {
    User user = new User(
            "0000000001",
            "testuser999",
            "testuser999@brockchelle.com"
    );
    User resultUser = userDB.pushUser(user);

    assertNotNull(resultUser);

    testUsers.add(resultUser);
  }

  @Test
  public void testEditUser() {
    User modifiedUser = new User(
            testUsers.get(0).getUUID(),
            "testuser999",
            "testuser999@brockchelle.com"
    );

    User resultUser = userDB.pushUser(modifiedUser);
    assertNotNull(resultUser);
  }

  @Test
  public void testFunctionalityTogether() {
    // Starts by reading the user, ensuring that it is correct
    User user = userDB.getUserByUUID("0000000000");

    assertEquals("0000000000", user.getUUID());
    assertEquals("testuser998", user.getUsername());
    assertEquals("testuser998@brockchelle.com", user.getEmail());

    // Modifies the user
    user = new User(
            user.getUUID(),
            "testuser999",
            "testuser999@brockchelle.com"
    );

    user = userDB.pushUser(user);
    assertNotNull(user);

    // Gets the modified user, ensures that the modification is correct
    user = userDB.getUserByUUID("0000000000");

    assertEquals("0000000000", user.getUUID());
    assertEquals("testuser999", user.getUsername());
    assertEquals("testuser999@brockchelle.com", user.getEmail());

    testUsers.set(0, user);

    // Pushes a user
    User newUser = new User(
            "0000000001",
            "testuser1000",
            "testuser1000@brockchelle.com"
    );

    newUser = userDB.pushUser(newUser);

    // Gets the new user, ensures that the push is correct
    newUser = userDB.getUserByUUID("0000000001");

    assertEquals("0000000001", newUser.getUUID());
    assertEquals("testuser1000", newUser.getUsername());
    assertEquals("testuser1000@brockchelle.com", newUser.getEmail());

    testUsers.add(newUser);
  }
}
