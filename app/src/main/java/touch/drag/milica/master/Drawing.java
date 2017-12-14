package touch.drag.milica.master;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import geometry.calculations.descretegeometrycalculations.PointInformations;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.HashMap;
import java.util.Vector;

public class Drawing extends Activity {

    private PointInformations pointInformations;
    private int state = 0;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    //  Log.i("OpenCV", "OpenCV loaded successfully");

                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        pointInformations = new PointInformations();

        ConstructionParser parser = new ConstructionParser();

        HashMap<String, Vector<String>> trics = new HashMap<>();

        parser.fillConstructions(trics, getApplicationContext(), "allconstuctions.json");

        DrawingView view = ((DrawingView) this.findViewById(R.id.view));
        view.setTrics(trics);
        view.setDensity(getResources().getDisplayMetrics().density, getResources().getDisplayMetrics().densityDpi);
        view.setTextView((TextView) this.findViewById(R.id.textView));

        this.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DrawingView) findViewById(R.id.view)).clearPanel();
            }
        });
        this.findViewById(R.id.undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DrawingView) findViewById(R.id.view)).undo();
            }
        });
        this.findViewById(R.id.redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DrawingView) findViewById(R.id.view)).redo();
            }
        });

        this.findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Drawing.this, Setting.class);

                intent.putExtra("moveColor", pointInformations.getMoveColor());
                intent.putExtra("fixedColor", pointInformations.getFixedColor());
                intent.putExtra("activeColor", pointInformations.getActiveColor());
                intent.putExtra("canChooseColor", pointInformations.getCanChooseColor());
                intent.putExtra("cannotChooseColor", pointInformations.getCannotChooseColor());
                intent.putExtra("otherColor", pointInformations.getOtherColor());

                intent.putExtra("pointSize", pointInformations.getPointSize());
                intent.putExtra("label", pointInformations.isLabel());
                intent.putExtra("textSize", pointInformations.getTextSize());

                startActivityForResult(intent, 17);
            }
        });


        final ImageButton options = (ImageButton) this.findViewById(R.id.options);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state += 1;
                state = state % 3;

                switch (state) {
                    case 1:
                        ((DrawingView) findViewById(R.id.view)).setMode(DrawingView.Mode.MODE_MOVE);
                        options.setBackgroundResource(R.drawable.ic_action_move_on);
                        break;
                    case 2:
                        ((DrawingView) findViewById(R.id.view)).setMode(DrawingView.Mode.MODE_SELECT);
                        options.setBackgroundResource(R.drawable.ic_action_select_on);
                        break;
                    default:
                        ((DrawingView) findViewById(R.id.view)).setMode(DrawingView.Mode.MODE_USUAL);
                        options.setBackgroundResource(R.drawable.ic_action_drawing);

                }
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 17) {
            Bundle extras = data.getExtras();

            pointInformations.setMoveColor(extras.getInt("moveColor"));
            pointInformations.setFixedColor(extras.getInt("fixedColor"));
            pointInformations.setActiveColor(extras.getInt("activeColor"));
            pointInformations.setCanChooseColor(extras.getInt("canChooseColor"));
            pointInformations.setCannotChooseColor(extras.getInt("cannotChooseColor"));
            pointInformations.setOtherColor(extras.getInt("otherColor"));

            pointInformations.setPointSize(extras.getFloat("pointSize"));
            pointInformations.setLabel(extras.getBoolean("label"));
            pointInformations.setTextSize(extras.getFloat("textSize"));
            ((DrawingView) findViewById(R.id.view)).setPointInformations(pointInformations);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
