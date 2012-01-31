package org.ugate.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import org.ugate.RemoteSettings;
import org.ugate.UGateKeeper;
import org.ugate.gui.components.Gauge.IndicatorType;
import org.ugate.gui.components.UGateGaugePreferenceView;
import org.ugate.gui.components.UGateTextFieldPreferenceView;
import org.ugate.resources.RS;

/**
 * Camera control view
 */
public class PositionSettings extends ControlPane {
	
	public static final int NUM_OF_SENSORS = 4;
	public static final int MAX_SENSOR_INDEX = PositionSettings.NUM_OF_SENSORS - 1;
	private UGateTextFieldPreferenceView sonarAnglePriority;
	private UGateTextFieldPreferenceView pirAnglePriority;
	private UGateTextFieldPreferenceView mwAnglePriority;
	private UGateTextFieldPreferenceView laserAnglePriority;
	
	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 */
	public PositionSettings(final ControlBar controlBar) {
		super(controlBar);
		addColumn1();
		addColumn2();
		addColumn3();
	}

	protected void addColumn1() {
		final GridPane grid = new GridPane();
		grid.setHgap(0d);
		grid.setVgap(0d);
		// cam pan
		final Label panHeader = createLabel("cam.pan");
		grid.add(panHeader, 0, 0);
		final UGateGaugePreferenceView camPanGauge = new UGateGaugePreferenceView(
				RemoteSettings.CAM_ANGLE_PAN, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(),
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0d, 180d, 19, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_CAM);
		controlBar.addHelpTextTrigger(camPanGauge, RS.rbLabel("cam.pan.desc"));
		grid.add(camPanGauge, 0, 1);
		// cam tilt
		final Label tiltHeader = createLabel("cam.tilt");
		grid.add(tiltHeader, 1, 0);
		final ImageView tiltImgView = RS.imgView(camPanGauge.imageView.getImage());
		tiltImgView.setRotate(90d);
		final UGateGaugePreferenceView camTiltGauge = new UGateGaugePreferenceView(
				RemoteSettings.CAM_ANGLE_TILT, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 0, FORMAT_ANGLE, tiltImgView, COLOR_CAM);
		controlBar.addHelpTextTrigger(camTiltGauge, RS.rbLabel("cam.tilt"));
		grid.add(camTiltGauge, 1, 1);
		// sonar/pir pan
		final Label sonarPirPanHeader = createLabel("sonarpir.pan");
		grid.add(sonarPirPanHeader, 0, 2);
		final UGateGaugePreferenceView sonarPirPanGauge = new UGateGaugePreferenceView(
				RemoteSettings.SONAR_IR_ANGLE_PAN, null, 
				UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_MULTI);
		controlBar.addHelpTextTrigger(sonarPirPanGauge, RS.rbLabel("sonarpir.pan.desc"));
		grid.add(sonarPirPanGauge, 0, 3);
		// sonar/pir tilt
		final Label sonarPirTiltHeader = createLabel("sonarpir.tilt");
		grid.add(sonarPirTiltHeader, 1, 2);
		final ImageView sonarPirTiltImgView = RS.imgView(sonarPirPanGauge.imageView.getImage());
		sonarPirTiltImgView.setRotate(90d);
		final UGateGaugePreferenceView sonarPirTiltGauge = new UGateGaugePreferenceView(
				RemoteSettings.SONAR_IR_ANGLE_TILT, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 0, FORMAT_ANGLE, sonarPirTiltImgView, COLOR_MULTI);
		controlBar.addHelpTextTrigger(sonarPirTiltGauge, RS.rbLabel("sonarpir.tilt.desc"));
		grid.add(sonarPirTiltGauge, 1, 3);
		// microwave pan
		final Label headerMW = createLabel("microwave.pan");
		grid.add(headerMW, 0, 4);
		final UGateGaugePreferenceView mwPanGauge = new UGateGaugePreferenceView(
				RemoteSettings.MW_ANGLE_PAN, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_MW);
		controlBar.addHelpTextTrigger(mwPanGauge, RS.rbLabel("microwave.pan.desc"));
		grid.add(mwPanGauge, 0, 5);
		
		final Group camCell = createCell(false, true, grid);
		add(camCell, 0, 0);
	}
	
	protected void addColumn2() {
		final GridPane grid = new GridPane();
		//####### Sonar #######
		sonarAnglePriority = new UGateTextFieldPreferenceView(
	    		RemoteSettings.CAM_SONAR_TRIP_ANGLE_PRIORITY, 
	    		UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
	    		PRIORITY_FORMAT, COLOR_SONAR, 0, MAX_SENSOR_INDEX, RS.rbLabel("cam.sonar.trip.angle.priority"), null);
		controlBar.addHelpTextTrigger(sonarAnglePriority, RS.rbLabel("cam.trip.angle.priority.desc",
				RS.rbLabel("cam.sonar.trip.angle.priority")));
		addPriorityValueListener(sonarAnglePriority);
		grid.add(sonarAnglePriority, 0, 0, 2, 1);
		// cam sonar pan
		final Label camSonarPanHeader = createLabel("cam.pan.sonar");
		grid.add(camSonarPanHeader, 0, 1);
		final UGateGaugePreferenceView camSonarPanGauge = new UGateGaugePreferenceView(
				RemoteSettings.CAM_SONAR_TRIP_ANGLE_PAN, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_SONAR);
		controlBar.addHelpTextTrigger(camSonarPanGauge, RS.rbLabel("cam.pan.sonar.desc"));
		grid.add(camSonarPanGauge, 0, 2);
		// cam sonar tilt
		final ImageView camSonarTiltImgView = RS.imgView(camSonarPanGauge.imageView.getImage());
		camSonarTiltImgView.setRotate(90d);
		final Label camSonarTiltHeader = createLabel("cam.tilt.sonar");
		grid.add(camSonarTiltHeader, 1, 1);
		final UGateGaugePreferenceView camSonarTiltGauge = new UGateGaugePreferenceView(
				RemoteSettings.CAM_SONAR_TRIP_ANGLE_TILT, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, camSonarTiltImgView, COLOR_SONAR);
		controlBar.addHelpTextTrigger(camSonarTiltGauge, RS.rbLabel("cam.tilt.sonar.desc"));
		grid.add(camSonarTiltGauge, 1, 2);
		//####### PIR #######
		pirAnglePriority = new UGateTextFieldPreferenceView(
	    		RemoteSettings.CAM_PIR_TRIP_ANGLE_PRIORITY, 
	    		UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
	    		PRIORITY_FORMAT, COLOR_PIR, 0, MAX_SENSOR_INDEX, RS.rbLabel("cam.pir.trip.angle.priority"), null);
		controlBar.addHelpTextTrigger(pirAnglePriority, RS.rbLabel("cam.trip.angle.priority.desc",
				RS.rbLabel("cam.pir.trip.angle.priority")));
		addPriorityValueListener(pirAnglePriority);
		grid.add(pirAnglePriority, 0, 3, 2, 1);
		// cam PIR pan
		final Label pirPanHeader = createLabel("cam.pan.pir");
		grid.add(pirPanHeader, 0, 4);
		final UGateGaugePreferenceView pirPanGauge = new UGateGaugePreferenceView(
				RemoteSettings.CAM_PIR_TRIP_ANGLE_PAN, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_PIR);
		controlBar.addHelpTextTrigger(pirPanGauge, RS.rbLabel("cam.pan.pir.desc"));
		grid.add(pirPanGauge, 0, 5);
		// cam PIR tilt
		final Label pirTiltHeader = createLabel("cam.tilt.pir");
		grid.add(pirTiltHeader, 1, 4);
		final UGateGaugePreferenceView pirTiltGauge = new UGateGaugePreferenceView(
				RemoteSettings.CAM_PIR_TRIP_ANGLE_TILT, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_PIR);
		controlBar.addHelpTextTrigger(pirTiltGauge, RS.rbLabel("cam.tilt.pir.desc"));
		grid.add(pirTiltGauge, 1, 5);
		
		final Group cell = createCell(false, true, grid);
		add(cell, 1, 0);
	}
	
	protected void addColumn3() {
		final GridPane grid = new GridPane();
		//####### Microwave #######
		mwAnglePriority = new UGateTextFieldPreferenceView(
	    		RemoteSettings.CAM_MW_TRIP_ANGLE_PRIORITY, 
	    		UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
	    		PRIORITY_FORMAT, COLOR_MW, 0, MAX_SENSOR_INDEX, RS.rbLabel("cam.mw.trip.angle.priority"), null);
		controlBar.addHelpTextTrigger(mwAnglePriority, RS.rbLabel("cam.trip.angle.priority.desc",
				RS.rbLabel("cam.mw.trip.angle.priority")));
		addPriorityValueListener(mwAnglePriority);
		grid.add(mwAnglePriority, 0, 0, 2, 1);
		// cam microwave pan
		final Label mwPanHeader = createLabel("cam.pan.microwave");
		grid.add(mwPanHeader, 0, 1);
		final UGateGaugePreferenceView mwPanGauge = new UGateGaugePreferenceView(
				RemoteSettings.CAM_MW_TRIP_ANGLE_PAN, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_MW);
		controlBar.addHelpTextTrigger(mwPanGauge, RS.rbLabel("cam.pan.microwave.desc"));
		grid.add(mwPanGauge, 0, 2);
		// cam microwave tilt
		final Label mwTiltHeader = createLabel("cam.tilt.microwave");
		grid.add(mwTiltHeader, 1, 1);
		final UGateGaugePreferenceView mwTiltGauge = new UGateGaugePreferenceView(
				RemoteSettings.CAM_MW_TRIP_ANGLE_TILT, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_MW);
		controlBar.addHelpTextTrigger(mwTiltGauge, RS.rbLabel("cam.tilt.microwave.desc"));
		grid.add(mwTiltGauge, 1, 2);
		//####### Laser #######
		laserAnglePriority = new UGateTextFieldPreferenceView(
	    		RemoteSettings.CAM_LASER_TRIP_ANGLE_PRIORITY, 
	    		UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
	    		PRIORITY_FORMAT, COLOR_LASER, 0, MAX_SENSOR_INDEX, RS.rbLabel("cam.laser.trip.angle.priority"), null);
		controlBar.addHelpTextTrigger(laserAnglePriority, RS.rbLabel("cam.trip.angle.priority.desc",
				RS.rbLabel("cam.laser.trip.angle.priority")));
		addPriorityValueListener(laserAnglePriority);
		grid.add(laserAnglePriority, 0, 3, 2, 1);
		// cam laser pan
		final Label laserPanHeader = createLabel("cam.pan.laser");
		grid.add(laserPanHeader, 0, 4);
		final UGateGaugePreferenceView laserPanGauge = new UGateGaugePreferenceView(
				RemoteSettings.CAM_LASER_TRIP_ANGLE_PAN, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_LASER);
		controlBar.addHelpTextTrigger(laserPanGauge, RS.rbLabel("cam.pan.laser.desc"));
		grid.add(laserPanGauge, 0, 5);
		// cam laser tilt
		final Label laserTiltHeader = createLabel("cam.tilt.laser");
		grid.add(laserTiltHeader, 1, 4);
		final UGateGaugePreferenceView laserTiltGauge = new UGateGaugePreferenceView(
				RemoteSettings.CAM_LASER_TRIP_ANGLE_TILT, null, UGateKeeper.DEFAULT.wirelessGetCurrentRemoteNodeIndex(), 
				IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10.7d, 0, 0, 180d, 18, 0, FORMAT_ANGLE, RS.IMG_PAN, COLOR_LASER);
		controlBar.addHelpTextTrigger(laserTiltGauge, RS.rbLabel("cam.tilt.laser.desc"));
		grid.add(laserTiltGauge, 1, 5);
		
		final Group cell = createCell(false, true, grid);
		add(cell, 2, 0);
	}
	
	protected void addPriorityValueListener(final UGateTextFieldPreferenceView control) {
		control.valueProperty().addListener(new ChangeListener<Object>() {
			private boolean init = false;
			@Override
			public void changed(final ObservableValue<? extends Object> observable, 
					final Object oldValue, final Object newValue) {
				if (init) {
					return;
				}
				init = true;
				/* TODO : enforce sensor priority
				final int diff = (Integer.valueOf(newValue.toString()) - Integer.valueOf(oldValue.toString())) / MAX_SENSOR_INDEX;
				if (control != sonarAnglePriority) {
					sonarAnglePriority.setValue((Integer) sonarAnglePriority.getValue() + diff);
				}
				if (control != pirAnglePriority) {
					pirAnglePriority.setValue((Integer) pirAnglePriority.getValue() + diff);
				}
				if (control != mwAnglePriority) {
					mwAnglePriority.setValue((Integer) mwAnglePriority.getValue() + diff);
				}
				if (control != laserAnglePriority) {
					laserAnglePriority.setValue((Integer) laserAnglePriority.getValue() + diff);
				}*/
				init = false;
			}
		});
	}
}
