package com.vitco.frames.engine;

import com.threed.jpct.Camera;
import com.threed.jpct.SimpleVector;
import com.vitco.res.VitcoSettings;

import java.util.ArrayList;

/**
 * Basic camera interaction.
 */
public class CCamera extends Camera {

    // remembers how much the camera has been shifted already
    private final float[] amountShifted = new float[2];

    // the zoom limit
    private float ZOOM_OUT_MAX;
    private float ZOOM_IN_MAX;

    public void setZoomLimits(float zoom_in_max, float zoom_out_max) {
        ZOOM_OUT_MAX = zoom_out_max;
        ZOOM_IN_MAX = zoom_in_max;
    }

    // to disable all camera interaction
    private boolean enableCamera = true;
    public void setEnabled(boolean b) {
        enableCamera = b;
    }

    // camera change listener
    private final ArrayList<CameraChangeListener> listener = new ArrayList<CameraChangeListener>();
    // add listener
    public void addCameraChangeListener(CameraChangeListener ccl) {
        listener.add(ccl);
    }
    // remove listener
    public void removeCameraChangeListener(CameraChangeListener ccl) {
        listener.remove(ccl);
    }
    // notify all listeners
    private void notifyListener() {

        for (CameraChangeListener ccl : listener) {
            ccl.onCameraChange();
        }
    }

    // zoom in
    public void zoomIn(float speed) {
        if (enableCamera) {
            // shift camera back
            this.moveCamera(Camera.CAMERA_MOVELEFT, -amountShifted[0]);
            this.moveCamera(Camera.CAMERA_MOVEUP, -amountShifted[1]);
            // check for zooming
            if (this.getPosition().distance(SimpleVector.ORIGIN) > ZOOM_IN_MAX) {
                this.moveCamera(Camera.CAMERA_MOVEIN, speed);
            }
            // shift the camera back (again)
            this.moveCamera(Camera.CAMERA_MOVEUP, amountShifted[1]);
            this.moveCamera(Camera.CAMERA_MOVELEFT, amountShifted[0]);
            notifyListener();
        }
    }

    // zoom out
    public void zoomOut(float speed) {
        if (enableCamera) {
            // shift camera back
            this.moveCamera(Camera.CAMERA_MOVELEFT, -amountShifted[0]);
            this.moveCamera(Camera.CAMERA_MOVEUP, -amountShifted[1]);
            // check for zooming
            if (this.getPosition().distance(new SimpleVector(0,0,0)) < ZOOM_OUT_MAX) {
                this.moveCamera(Camera.CAMERA_MOVEIN, -speed);
            }
            // shift the camera back (again)
            this.moveCamera(Camera.CAMERA_MOVEUP, amountShifted[1]);
            this.moveCamera(Camera.CAMERA_MOVELEFT, amountShifted[0]);
            notifyListener();
        }
    }

    // reset the perspective
    public void setView(SimpleVector pos) {
        if (enableCamera) {
            this.setPosition(pos);
            this.lookAt(SimpleVector.ORIGIN);
            // reset internal shift vars
            amountShifted[0] = 0;
            amountShifted[1] = 0;

            notifyListener();
        }
    }

    // rotate view
    public void rotate(float amountX, float amountY) {
        if (enableCamera) {
            // shift camera back
            this.moveCamera(Camera.CAMERA_MOVELEFT, -amountShifted[0]);
            this.moveCamera(Camera.CAMERA_MOVEUP, -amountShifted[1]);

            // move to origin
            float dist = this.getPosition().distance(SimpleVector.ORIGIN);
            this.moveCamera(Camera.CAMERA_MOVEIN, dist);

            // rotate the camera correctly
            this.rotateAxis(this.getYAxis(), amountX * VitcoSettings.MAIN_VIEW_ROTATION_Y_FACTOR);
            this.rotateX(-amountY * VitcoSettings.MAIN_VIEW_ROTATION_X_FACTOR);

            // move the camera out again
            this.moveCamera(Camera.CAMERA_MOVEOUT, dist);

            // shift the camera back (again)
            this.moveCamera(Camera.CAMERA_MOVEUP, amountShifted[1]);
            this.moveCamera(Camera.CAMERA_MOVELEFT, amountShifted[0]);

            notifyListener();
        }
    }

    // shift view
    public void shift(float amountX, float amountY, float factor) {
        if (enableCamera) {
            amountShifted[0] += amountX*factor;
            this.moveCamera(Camera.CAMERA_MOVELEFT, amountX * factor);
            amountShifted[1] += amountY*factor;
            this.moveCamera(Camera.CAMERA_MOVEUP, amountY * factor);
            notifyListener();
        }
    }
}
