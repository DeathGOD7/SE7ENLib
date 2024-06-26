// This file is part of SE7ENLib, created on 20/04/2024 (23:14 PM)
// Name : PoolSettings
// Author : Death GOD 7

package io.github.deathgod7.SE7ENLib.database;

/**
 * Represents the Pool Settings
 * @version 1.0
 * @since 1.0
 */
public class PoolSettings {
	int minIdleConnections;
	int maxPoolSize;
	long connectionTimeout;
	long idleTimeout;
	long maxLifetime;

	/**
	 * Creates a new PoolSettings object with default values
	 */
	public PoolSettings() {
		this.minIdleConnections = 5;
		this.maxPoolSize = 10;
		this.connectionTimeout = 30000;
		this.idleTimeout = 600000;
		this.maxLifetime = 1800000;
	}

	/**
	 * Creates a new PoolSettings object with the given values
	 * @param minIdleConnections the minimum idle connections that should be kept in the pool
	 * @param maxPoolSize the maximum pool size that should be in the pool
	 * @param connectionTimeout the connection timeout that should be used
	 * @param idleTimeout the idle timeout
	 * @param maxLifetime the maximum lifetime
	 */
	public PoolSettings(int minIdleConnections, int maxPoolSize, long connectionTimeout, long idleTimeout, long maxLifetime) {
		this.minIdleConnections = minIdleConnections;
		this.maxPoolSize = maxPoolSize;
		this.connectionTimeout = connectionTimeout;
		this.idleTimeout = idleTimeout;
		this.maxLifetime = maxLifetime;
	}

	/**
	 * Sets the minimum idle connections
	 * @param minIdleConnections the minimum idle connections
	 */
	public void setMinIdleConnections(int minIdleConnections) {
		this.minIdleConnections = minIdleConnections;
	}

	/**
	 * Returns the pool minimum idle connections
	 * @return {@link int}
	 */
	public int getMinIdleConnections() {
		return minIdleConnections;
	}

	/**
	 * Sets the maximum pool size
	 * @param maxPoolSize the maximum pool size
	 */
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	/**
	 * Returns the pool maximum size
	 *
	 * @return {@link int}
	 */
	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	/**
	 * Sets the pool connection timeout
	 * @param connectionTimeout the connection timeout
	 */
	public void setConnectionTimeout(long connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	/**
	 * Returns the pool connection timeout
	 *
	 * @return {@link long}
	 */
	public long getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * Sets the pool idle timeout
	 * @param idleTimeout the idle timeout
	 */
	public void setIdleTimeout(long idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	/**
	 * Returns the pool idle timeout
	 *
	 * @return {@link long}
	 */
	public long getIdleTimeout() {
		return idleTimeout;
	}

	/**
	 * Sets the pool maximum lifetime
	 * @param maxLifetime the maximum lifetime
	 */
	public void setMaxLifetime(long maxLifetime) {
		this.maxLifetime = maxLifetime;
	}

	/**
	 * Returns the pool maximum lifetime
	 *
	 * @return {@link long}
	 */
	public long getMaxLifetime() {
		return maxLifetime;
	}

}
