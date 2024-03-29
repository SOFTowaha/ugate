package org.ugate.gui.view;

import java.util.Calendar;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.UGateEvent;
import org.ugate.UGateKeeper;
import org.ugate.UGateListener;
import org.ugate.UGateUtil;
import org.ugate.gui.ControlBar;
import org.ugate.gui.GuiUtil;
import org.ugate.gui.components.Digits;
import org.ugate.gui.components.UGateToggleSwitchBox;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEY;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.service.entity.jpa.RemoteNodeReading;
import org.ugate.wireless.data.RxTxRemoteNodeReadingDTO;

/**
 * {@linkplain RemoteNodeReading} view
 */
public class SensorReading extends Parent {

	private static final Logger log = LoggerFactory.getLogger(SensorReading.class);
	private final ControlBar cb;
	private final Label readDate;
	private final Digits sonarReading;
	private final Digits pirReading;
	private final Digits mwReading;
	private final Digits laserReading;
	private final UGateToggleSwitchBox<RemoteNode> reportReadings;
	private final ReadOnlyObjectWrapper<RemoteNodeReading> sensorReadingsPropertyWrapper;
	public static final double CHILD_SPACING = 10d;
	public static final double CHILD_PADDING = 5d;
	public static final Insets PADDING_INSETS = new Insets(CHILD_PADDING, CHILD_PADDING, CHILD_PADDING, CHILD_PADDING);

	/**
	 * Constructor
	 * 
	 * @param controlBar
	 *            the {@linkplain ControlBar}
	 * @param orientation
	 *            the {@linkplain Orientation} of the
	 *            {@linkplain SensorReading}
	 */
	public SensorReading(final ControlBar controlBar, final Orientation orientation) {
		super();
		this.cb = controlBar;
		this.sensorReadingsPropertyWrapper = new ReadOnlyObjectWrapper<RemoteNodeReading>();
		readDate = new Label();
		readDate.getStyleClass().add("readings-text");
		cb.addHelpTextTrigger(readDate, RS.rbLabel(KEY.WIRELESS_REMOTE_READINGS_TIME));
		final ImageView sonarReadingLabel = RS.imgView(RS.IMG_SONAR);
		sonarReading = new Digits(String.format(AlarmThresholds.FORMAT_SONAR, 0.0f),
				0.15f, GuiUtil.COLOR_SONAR, null);
		cb.addHelpTextTrigger(sonarReading, RS.rbLabel(KEY.WIRELESS_REMOTE_READINGS_SENSOR));
		final ImageView pirReadingLabel = RS.imgView(RS.IMG_PIR);
		pirReading = new Digits(String.format(AlarmThresholds.FORMAT_PIR, 0), 
				0.15f, GuiUtil.COLOR_PIR, null);
		cb.addHelpTextTrigger(pirReading, RS.rbLabel(KEY.WIRELESS_REMOTE_READINGS_SENSOR));
		final ImageView mwReadingLabel = RS.imgView(RS.IMG_MICROWAVE);
		mwReading = new Digits(String.format(AlarmThresholds.FORMAT_MW, 0), 0.15f, 
				GuiUtil.COLOR_MW, null);
		cb.addHelpTextTrigger(mwReading, RS.rbLabel(KEY.WIRELESS_REMOTE_READINGS_SENSOR));
		final ImageView laserReadingLabel = RS.imgView(RS.IMG_LASER);
		laserReading = new Digits(String.format(AlarmThresholds.FORMAT_LASER, 0.0f), 
				0.15f, GuiUtil.COLOR_LASER, null);
		cb.addHelpTextTrigger(laserReading, RS.rbLabel(KEY.WIRELESS_REMOTE_READINGS_SENSOR));
		reportReadings = new UGateToggleSwitchBox<>(
				controlBar.getRemoteNodePA(), RemoteNodeType.REPORT_READINGS);
		cb.addHelpTextTrigger(reportReadings, RS.rbLabel(KEY.WIRELESS_REMOTE_READINGS_REPORT));
		final GridPane readingsGroup = GuiUtil.createBackgroundDisplay(PADDING_INSETS, CHILD_SPACING, 
				orientation == Orientation.HORIZONTAL ? 10 : 1, true,
				0, 0, readDate, sonarReadingLabel, sonarReading, pirReadingLabel, pirReading, 
				mwReadingLabel, mwReading, laserReadingLabel, laserReading, reportReadings);
		// show a visual indication that the settings need updated
		UGateKeeper.DEFAULT.addListener(new UGateListener() {
			@Override
			public void handle(final UGateEvent<?, ?> event) {
				if (event.getType() == UGateEvent.Type.WIRELESS_DATA_RX_SUCCESS) {
					final RemoteNode rn = (RemoteNode) event.getSource();
					if (event.getNewValue() instanceof RxTxRemoteNodeReadingDTO && 
							rn.getAddress().equalsIgnoreCase(cb.getRemoteNode().getAddress())) {
						final RxTxRemoteNodeReadingDTO sr = (RxTxRemoteNodeReadingDTO) event.getNewValue();
						remoteNodeReadingShow(sr.getRemoteNodeReading());
					}
				} else if (event.getType() == UGateEvent.Type.APP_DATA_LOADED || 
						event.getType() == UGateEvent.Type.WIRELESS_REMOTE_NODE_CHANGED) {
					remoteNodeReadingShow(true);
				}
			}
		});
		getChildren().add(readingsGroup);
	}

	/**
	 * Sets the {@linkplain RemoteNodeReading} values in the
	 * {@linkplain ControlBar} to the last read from the device
	 * 
	 * @param isReset
	 *            true when the readings should be reset when no readings exist
	 */
	public void remoteNodeReadingShow(final boolean isReset) {
		try {
			final RemoteNodeReading rnr = ServiceProvider.IMPL
					.getRemoteNodeService().findReadingLatest(cb.getRemoteNode());
			if (rnr != null) {
				remoteNodeReadingShow(rnr);
			} else if (isReset) {
				remoteNodeReadingShow(null);
			}
		} catch (final Throwable t) {
			log.warn(String.format("Unable to get %1$s(s) for %2$s: %3$s", 
					RemoteNodeReading.class.getName(), RemoteNode.class.getName(), 
					cb.getRemoteNode().getAddress()));
		}
	}

	/**
	 * Sets the {@linkplain RemoteNodeReading} values in the
	 * {@linkplain ControlBar}
	 * 
	 * @param remoteNodeReading
	 *            the {@linkplain RemoteNodeReading} to set
	 */
	public void remoteNodeReadingShow(final RemoteNodeReading remoteNodeReading) {
		if (remoteNodeReading != null && 
				cb.getRemoteNode().getId() == remoteNodeReading.getRemoteNode().getId()) {
			final Calendar cal = Calendar.getInstance();
			cal.setTime(remoteNodeReading.getReadDate());
			//sensorReadingsPropertyWrapper.set(arg0)
			readDate.setText(UGateUtil.calFormat(cal).replace(' ', '\n'));
			pirReading.setValue(String.format(AlarmThresholds.FORMAT_PIR, 
					remoteNodeReading.getPirIntensity()));
			if (cb.getActor().getHost().getUseMetric()) {
				sonarReading.setValue(String.format(AlarmThresholds.FORMAT_SONAR, 
						remoteNodeReading.getSonarMeters()));
				laserReading.setValue(String.format(AlarmThresholds.FORMAT_LASER, 
						remoteNodeReading.getLaserMeters()));
				mwReading.setValue(String.format(AlarmThresholds.FORMAT_MW, 
						Math.round(remoteNodeReading.getMicrowaveSpeedMillimetersPerSec())));
			} else {
				sonarReading.setValue(String.format(AlarmThresholds.FORMAT_SONAR, 
						Double.parseDouble(remoteNodeReading.getSonarFeet() + 
								"." + remoteNodeReading.getSonarInches())));
				laserReading.setValue(String.format(AlarmThresholds.FORMAT_LASER, 
						Double.parseDouble(remoteNodeReading.getLaserFeet() + 
								"." + remoteNodeReading.getLaserInches())));
				mwReading.setValue(String.format(AlarmThresholds.FORMAT_MW, 
						Math.round(remoteNodeReading.getMicrowaveSpeedMPH())));
			}
		} else if (remoteNodeReading == null) {
			readDate.setText(UGateUtil.calFormatBlank().replace(' ', '\n'));
			pirReading.setValue(String.format(AlarmThresholds.FORMAT_PIR, 0));
			sonarReading.setValue(String.format(AlarmThresholds.FORMAT_SONAR, 0d));
			laserReading.setValue(String.format(AlarmThresholds.FORMAT_LASER, 0d));
			mwReading.setValue(String.format(AlarmThresholds.FORMAT_MW, 0));
		}
		sensorReadingsPropertyWrapper.set(remoteNodeReading);
	}

	/**
	 * @return the {@linkplain RemoteNodeReading} property
	 */
	public ReadOnlyObjectProperty<RemoteNodeReading> sensorReadingsProperty() {
		return sensorReadingsPropertyWrapper.getReadOnlyProperty();
	}
}
