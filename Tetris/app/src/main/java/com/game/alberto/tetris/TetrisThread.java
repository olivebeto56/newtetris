package com.game.alberto.tetris;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by armando on 11/12/14.
 */
public class TetrisThread extends Thread {

    private SurfaceHolder _surfaceHolder;
    private TetriSurface _panel;
    private boolean _run = false;

    public TetrisThread(SurfaceHolder surfaceHolder, TetriSurface panel) {
        _surfaceHolder = surfaceHolder;
        _panel = panel;
    }

    public void setRunning(boolean run) {
        _run = run;
    }

    @Override
    public void run() {
        Canvas c;

        while (_run) {
            c = null;
            try {
                c = _surfaceHolder.lockCanvas(null);
                if (c != null)
                {
                    synchronized (_surfaceHolder) {
                        _panel.onDraw(c);

                    }
                }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    _surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }

}