package org.ugate.service.entity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * {@linkplain RemoteNode} {@linkplain IModelType} related to an individual node
 * devices located in a remote locations. Each {@linkplain #ordinal()}
 * represents a the proper order that incoming data is received from remote node
 * devices.
 */
public enum RemoteNodeType implements IModelType<RemoteNode> {
	DEVICE_SOUNDS_ON("deviceSoundsOn", false), CAM_IMG_CAPTURE_RETRY_CNT(
			"camImgCaptureRetryCnt", false), WIRELESS_ADDRESS("address", false), WIRELESS_WORKING_DIR_PATH(
			"workingDir", false), MAIL_ALERT_ON("mailAlertOn", false), UNIVERSAL_REMOTE_ACCESS_ON(
			"universalRemoteAccessOn", true), UNIVERSAL_REMOTE_ACCESS_CODE_1(
			"universalRemoteAccessCode1", true), UNIVERSAL_REMOTE_ACCESS_CODE_2(
			"universalRemoteAccessCode2", true), UNIVERSAL_REMOTE_ACCESS_CODE_3(
			"universalRemoteAccessCode3", true), GATE_ACCESS_ON("gateAccessOn",
			true), SONAR_DISTANCE_THRES_FEET("sonarDistanceThresFeet", true), SONAR_DISTANCE_THRES_INCHES(
			"sonarDistanceThresInches", true), SONAR_DELAY_BTWN_TRIPS(
			"sonarDelayBtwnTrips", true), PIR_DELAY_BTWN_TRIPS(
			"pirDelayBtwnTrips", true), SONAR_PIR_ANGLE_PAN("sonarPirAnglePan",
			true), SONAR_PIR_ANGLE_TILT("sonarPirAngleTilt", true), MW_SPEED_THRES_CYCLES_PER_SEC(
			"mwSpeedThresCyclesPerSec", true), MW_DELAY_BTWN_TRIPS(
			"mwDelayBtwnTrips", true), MW_ANGLE_PAN("mwAnglePan", true), LASER_DISTANCE_THRES_FEET(
			"laserDistanceThresFeet", true), LASER_DISTANCE_THRES_INCHES(
			"laserDistanceThresInches", true), LASER_DELAY_BTWN_TRIPS(
			"laserDelayBtwnTrips", true), LASER_ANGLE_PAN("laserAnglePan", true), LASER_ANGLE_TILT(
			"laserAngleTilt", true), MULTI_ALARM_TRIP_STATE(
			"multiAlarmTripState", true), CAM_RESOLUTION("camResolution", true), CAM_ANGLE_PAN(
			"camAnglePan", true), CAM_ANGLE_TILT("camAngleTilt", true), CAM_SONAR_TRIP_ANGLE_PRIORITY(
			"camSonarTripAnglePriority", true), CAM_SONAR_TRIP_ANGLE_PAN(
			"camSonarTripAnglePan", true), CAM_SONAR_TRIP_ANGLE_TILT(
			"camSonarTripAngleTilt", true), CAM_PIR_TRIP_ANGLE_PRIORITY(
			"camPirTripAnglePriority", true), CAM_PIR_TRIP_ANGLE_PAN(
			"camPirTripAnglePan", true), CAM_PIR_TRIP_ANGLE_TILT(
			"camPirTripAngleTilt", true), CAM_MW_TRIP_ANGLE_PRIORITY(
			"camMwTripAnglePriority", true), CAM_MW_TRIP_ANGLE_PAN(
			"camMwTripAnglePan", true), CAM_MW_TRIP_ANGLE_TILT(
			"camMwTripAngleTilt", true), CAM_LASER_TRIP_ANGLE_PRIORITY(
			"camLaserTripAnglePriority", true), CAM_LASER_TRIP_ANGLE_PAN(
			"camLaserTripAnglePan", true), CAM_LASER_TRIP_ANGLE_TILT(
			"camLaserTripAngleTilt", true);

	public static final int WIRELESS_ADDRESS_MAX_DIGITS = 4;
	private static AtomicInteger canRemoteCount = new AtomicInteger(-1);

	private final String key;
	private final boolean canRemote;

	/**
	 * Constructor
	 * 
	 * @param key
	 *            {@linkplain #getKey()}
	 * @param canRemote
	 *            {@linkplain #canRemote()}
	 */
	private RemoteNodeType(final String key, final boolean canRemote) {
		this.key = key;
		this.canRemote = canRemote;
	}

	/**
	 * @param host
	 *            the relative {@linkplain Host} of the {@linkplain RemoteNode}
	 * @return a new default {@linkplain RemoteNode}
	 */
	public static RemoteNode newDefaultRemoteNode(final Host host) {
		if (host == null) {
			throw new NullPointerException(Host.class.getName()
					+ " cannot be null");
		}
		final RemoteNode remoteNode = new RemoteNode();
		remoteNode.setHost(host);
		remoteNode.setAddress("3333");
		remoteNode.setDeviceSoundsOn(1);
		remoteNode.setGateAccessOn(1);
		remoteNode.setMailAlertOn(1);
		remoteNode.setUniversalRemoteAccessOn(0);
		remoteNode.setCreatedDate(new Date());
		return remoteNode;
	}

	/**
	 * @return the number of elements that can remote
	 */
	public static int canRemoteCount() {
		if (canRemoteCount.get() < 0) {
			canRemoteCount.incrementAndGet();
			for (final RemoteNodeType rnt : values()) {
				if (rnt.canRemote()) {
					canRemoteCount.incrementAndGet();
				}
			}
		}
		return canRemoteCount.get();
	}

	/**
	 * Gets a {@linkplain RemoteNode} value for a {@linkplain #canRemote()}
	 * {@linkplain RemoteNodeType}
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode} to get the value from
	 * @return the extracted {@linkplain RemoteNode} value
	 * @throws Throwable
	 *             any errors during extraction
	 */
	public Integer getRemoteValue(final RemoteNode remoteNode) throws Throwable {
		return getOrSetRemoteValue(remoteNode, null);
	}

	/**
	 * Sets a {@linkplain RemoteNode} value for a {@linkplain #canRemote()}
	 * {@linkplain RemoteNodeType}.
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode} to get the value from
	 * @param value
	 *            the value to set
	 * @return the extracted {@linkplain RemoteNode} value
	 * @throws Throwable
	 *             any errors during extraction
	 */
	public void setRemoteValue(final RemoteNode remoteNode, final int value)
			throws Throwable {
		getOrSetRemoteValue(remoteNode, value);
	}

	/**
	 * Gets/Sets a {@linkplain RemoteNode} value for a {@linkplain #canRemote()}
	 * {@linkplain RemoteNodeType}
	 * 
	 * @param remoteNode
	 *            the {@linkplain RemoteNode} to get the value from
	 * @param value
	 *            the value to set
	 * @return the extracted {@linkplain RemoteNode} value
	 * @throws Throwable
	 *             any errors during extraction
	 */
	private Integer getOrSetRemoteValue(final RemoteNode remoteNode,
			final Integer value) throws Throwable {
		if (!canRemote()) {
			throw new UnsupportedOperationException(this
					+ " must have remote capabilities");
		}
		final String methodName = buildMethodName(
				value == null ? "get" : "set", getKey());
		final MethodHandle mh = MethodHandles
				.lookup()
				.findVirtual(
						RemoteNode.class,
						methodName,
						MethodType.methodType(RemoteNode.class.getMethod(
								methodName).getReturnType()))
				.bindTo(remoteNode);
		if (value == null) {
			return (int) mh.invoke();
		} else {
			mh.invoke((int) value);
			return null;
		}
	}

	/**
	 * Builds a method name using a prefix and a field name
	 * 
	 * @param prefix
	 *            the method's prefix
	 * @param fieldName
	 *            the method's field name
	 * @return the method name
	 */
	private static String buildMethodName(final String prefix,
			final String fieldName) {
		return (fieldName.startsWith(prefix) ? fieldName : prefix
				+ fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%1$s (key = %2$s, canRemote = %3$s)",
				super.toString(), key, canRemote);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getKey() {
		return this.key;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canRemote() {
		return this.canRemote;
	}
}
