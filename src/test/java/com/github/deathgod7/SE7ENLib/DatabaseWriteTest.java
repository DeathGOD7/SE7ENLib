// This file is part of SE7ENLib, created on 18/10/2023 (06:05 AM)
// Name : ReadDB
// Author : Death GOD 7

package com.github.deathgod7.SE7ENLib;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseWriteTest {

	public String GetDatabasePath() {
		Path resourceDirectory = Paths.get("src","test","resources");
		String absolutePath = resourceDirectory.toFile().getAbsolutePath().replace('\\', '/');

		System.out.println(absolutePath);
		return absolutePath;
	}

	public void CheckPath() {
		assertTrue(GetDatabasePath().endsWith("src/test/resources"));
	}
}
