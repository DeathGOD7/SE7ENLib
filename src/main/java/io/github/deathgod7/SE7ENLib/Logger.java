// This file is part of SE7ENLib, created on 17/04/2024 (18:01 PM)
// Name : Logger
// Author : Death GOD 7

package io.github.deathgod7.SE7ENLib;

import io.github.deathgod7.SE7ENLib.database.DatabaseManager;

/**
 * Represents the Logger
 * @version 1.0
 * @since 1.0
 */
public class Logger {
	/**
	 * Util function to log the message
	 * @param message The message to log
	 */
	public static void log(String message) {
		boolean x = DatabaseManager.getInstance().getDebugMode();
		if (x) {
			System.out.println(message);
		}
	}
}
