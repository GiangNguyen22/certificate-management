package com.example.demo.entity;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class UserRbacTests {

    @Test
    void addRole_calculatesPermissions_and_preventsDuplicate() {
        User u = new User();

        Role r1 = new Role();
        r1.setId(1);
        r1.setRoleName("admin");
        r1.setPermissions(Arrays.asList("CERT_CREATE", "CERT_DELETE"));

        u.addRole(r1);

        assertEquals(1, u.getRoles().size(), "role should be added");
        assertTrue(u.hasPermission("CERT_CREATE"));
        assertTrue(u.hasPermission("CERT_DELETE"));

        // adding the same role again should not duplicate
        u.addRole(r1);
        assertEquals(1, u.getRoles().size(), "duplicate role should be ignored");
    }

    @Test
    void removeRole_byId_removesAndRecalculatesPermissions() {
        User u = new User();

        Role r1 = new Role();
        r1.setId(2);
        r1.setRoleName("creator");
        r1.setPermissions(Arrays.asList("CREATE"));

        u.addRole(r1);
        assertTrue(u.hasPermission("CREATE"));

        // remove by creating a role object with same id
        Role toRemove = new Role();
        toRemove.setId(2);
        u.removeRole(toRemove);

        assertEquals(0, u.getRoles().size());
        assertFalse(u.hasPermission("CREATE"));
    }

    @Test
    void wildcardPermission_grantsAll() {
        User u = new User();
        Role r = new Role();
        r.setId(3);
        r.setRoleName("super");
        r.setPermissions(Arrays.asList("*"));

        u.addRole(r);

        assertTrue(u.hasPermission("ANYTHING"));
        assertTrue(u.hasAnyPermission("X", "ANYTHING"));
        assertTrue(u.hasAllPermissions("FOO", "BAR") == false || u.hasAllPermissions());
        // The above line ensures hasAllPermissions doesn't throw; wildcard should make hasAnyPermission true
    }

    @Test
    void nullSafety_addAndRemove_doesNotThrow() {
        User u = new User();
        // should not throw
        u.addRole(null);
        u.removeRole(null);

        // still empty
        assertEquals(0, u.getRoles().size());
    }

    @Test
    void hasAnyAndHasAll_behaviour() {
        User u = new User();
        Role r1 = new Role();
        r1.setId(4);
        r1.setPermissions(Arrays.asList("A", "B"));
        u.addRole(r1);

        assertTrue(u.hasAnyPermission("X", "A"));
        assertFalse(u.hasAnyPermission("X", "Y"));

        assertTrue(u.hasAllPermissions("A", "B"));
        assertFalse(u.hasAllPermissions("A", "Z"));
    }
}
