package ca.myairbuddyandi;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by Michel on 2017-10-10.
 * Holds all of the logic for the EmergencyView class
 */

public class EmergencyView extends View {

    // Static
    private static final String LOG_TAG = "EmergencyView";

    // Public

    // Protected

    // Private

    private int mHeight; // Total height of the graphic
    private int mWidth; // Total width of the graphic
    private ArrayList<DiveSegment> mDiveSegmentAscentList = new ArrayList<>(); // Contains the Ascent numbers
    private final Context mContext;
    private DiveSegmentDetail mDiveSegmentDetail; // Contains the general milestones numbers
    private Paint mPaintText; // To paint text
    private Paint mPaintLine; // To paint line
    private final Rect mBounds = new Rect();
    private String mPressureUnit;
    private String mRateUnit;
    private String mStart;
    private String mDepthUnit;
    private String mTimeUnit;
    private String mVolumeUnit;
    private String mStop;
    private String mAscent;
    private String mRunningTotals;
    private String mRunningAscent;

    // End of variables

    // Public constructor
    public EmergencyView(Context context) {
        super(context);
        mContext = context;
        setupPaint();
        Log.d(LOG_TAG, "constructor done");
    }

    // Public constructor
    public EmergencyView(Context context, @Nullable AttributeSet attrs) {
        // Normal point of entry
        super(context, attrs);
        mContext = context;
        setupPaint();
        Log.d(LOG_TAG, "constructor done");
    }

    // Public constructor
    public EmergencyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setupPaint();
        Log.d(LOG_TAG, "constructor done");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        DiveSegment diveSegment;
        double depthPercentd;

        float anchorLineRatio; // Ratio to find position of Anchor line from left margin
        float bottomLineRatio; // Ratio to find the position of the Bottom line from top margin
        float surfaceLineRatio; // Ratio to find the position of the Surface line from top margin
        float runningTotalsLineRatio; // Ratio to find the position of the Running Totals line from top margin

        float runningTotalsLineRatioSmall = 0.0719671007539411f; // Ratio to find the position of the Running Totals line from top margin 105 / 1459
        float surfaceLineRatioSmall = 0.1480466072652502f; // Ratio to find the position of the Surface line from top margin 216 / 1459
        float bottomLineRatioSmall = 0.864290610006854f; // Ratio to find the position of the Bottom line from top margin 1261 / 1459
        float anchorLineRatioSmall = 0.495833333333333f; // Ratio to find position of Anchor line from left margin 535.5 / 1080

        float runningTotalsLineRatioLarge = 0.1190243902439024f; // Ratio to find the position of the Running Totals line from top margin 122 / 1025
        float surfaceLineRatioLarge = 0.1863414634146341f; // Ratio to find the position of the Surface line from top margin 191 / 1025
        float bottomLineRatioLarge = 0.864290610006854f; // Ratio to find the position of the Bottom line from top margin 953 / 1025
        float anchorLineRatioLarge = 0.495833333333333f; // Ratio to find position of Anchor line from left margin 439.5 / 800

        float bottomLineY; // Scaled position of the Bottom line
        float surfaceLineY; // Scaled position of the Surface line
        // Thickness of the Anchor line
        Float anchorLineThickness = 1.0f;
        // Thickness of the Bottom line
        float bottomLineThickness = 5.0f;
        float xPos; // x position to start the actual drawing of text
        float xPosFarLeft = 5.0f; // x position at the far left of the screen
        float yPos;// y position to start the actual drawing of text
        float textHeight;
        float diveHeight;
        float depthPercentf;
        int markerLineLength = 25; // Length of the marker on either the left or right side of the Anchor line
        String text;

        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==  Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            // on a large screen device
            anchorLineRatio = anchorLineRatioLarge;
            bottomLineRatio = bottomLineRatioLarge;
            surfaceLineRatio = surfaceLineRatioLarge;
            runningTotalsLineRatio = runningTotalsLineRatioLarge;
        } else {
            // On a small screen device
            anchorLineRatio = anchorLineRatioSmall;
            bottomLineRatio = bottomLineRatioSmall;
            surfaceLineRatio = surfaceLineRatioSmall;
            runningTotalsLineRatio = runningTotalsLineRatioSmall;
        }

        // Scaled position of the Anchor line
        Float anchorLineX = mWidth * anchorLineRatio;
        surfaceLineY = mHeight * surfaceLineRatio;
        bottomLineY = mHeight * bottomLineRatio;
        diveHeight = bottomLineY - surfaceLineY;

        // NOTE: Draw the FIXED set of numbers

        // Running time and volume
        // Located at the right of the anchor line
        // Between the flag and the buoy
        text = mRunningTotals;
        text += ": " + MyFunctions.convertMinToString(mDiveSegmentDetail.getRunningTotalTime());
        text += " " + mTimeUnit;
        text += ": " + MyFunctions.roundUp(mDiveSegmentDetail.getRunningTotalPressure(), 1);
        text += " " + mPressureUnit;
        text += ", " + MyFunctions.roundUp(mDiveSegmentDetail.getRunningTotalVolume(), 1);
        text += " " + mVolumeUnit;

        yPos = (mHeight * runningTotalsLineRatio);

        canvas.drawText(text, anchorLineX + anchorLineThickness + markerLineLength, yPos, mPaintText);

        // Running Ascent Time
        // Located to the right of the anchor line
        // Between the flag and the buoy
        // Below the Running time and volume

        text = mRunningAscent;
        text += ": " + MyFunctions.convertMinToString(mDiveSegmentDetail.getRunningAscentTime());
        text += " " + mTimeUnit;
        text += ": " + MyFunctions.roundUp(mDiveSegmentDetail.getRunningAscentPressure(), 1);
        text += " " + mPressureUnit;
        text += ", " + MyFunctions.roundUp(mDiveSegmentDetail.getRunningAscentVolume(), 1);
        text += " " + mVolumeUnit;
        textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);

        yPos = yPos + textHeight + 10;

        canvas.drawText(text, anchorLineX + anchorLineThickness + markerLineLength, yPos, mPaintText);

        // Start
        // Located at the top left
        // Example: Start 3442.0 psi
        //          Descent @ 30 ft/min
        text = mStart;
        text += " " + mDiveSegmentDetail.getTurnaroundPressure();
        text += " " + mPressureUnit;
        textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);

        yPos = (mHeight * surfaceLineRatio) + textHeight;
        canvas.drawText(text, xPosFarLeft, yPos, mPaintText);

        // Stop
        // Located at the top right
        // Example: Start 500.0 psi
        text = mStop;
        text += " " + mDiveSegmentDetail.getEndingPressure();
        text += " " + mPressureUnit;
        textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);

        yPos = (mHeight * surfaceLineRatio) + textHeight;
        canvas.drawText(text, anchorLineX + anchorLineThickness + markerLineLength, yPos, mPaintText);

        // NOTE: Draw the VARIABLE set of numbers

        // The Out of Air Situation (OOA)is located to the left of the Anchor line and below the Bottom line
        // No Marker line
        // No Descend rate
        // The OOA segment is always the last one in the list
        diveSegment = mDiveSegmentAscentList.get(mDiveSegmentAscentList.size()-1);
        text = mContext.getResources().getString(R.string.lbl_out_of_air);
        textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);
        yPos = (mHeight * bottomLineRatio) + bottomLineThickness + textHeight;
        canvas.drawText(text, 0, yPos, mPaintText);

        // Time and Depth
        text = MyFunctions.convertMinToString(diveSegment.getMinute());
        text += " " + mTimeUnit;
        text += " @ " + diveSegment.getDepth();
        text += " " + mDepthUnit;
        textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);

        yPos += textHeight;
        canvas.drawText(text, 0, yPos, mPaintText);

        // psi
        text = String.valueOf(diveSegment.getAirConsumptionPressure());
        text += " " + mPressureUnit;
        textHeight = MyFunctionsSegments.getTextHeightTaller(mPaintText);

        yPos += textHeight;
        canvas.drawText(text, 0, yPos, mPaintText);

        // The Ascent set of numbers
        // Drawing from surface to bottom, from Safety Stop to Start of Ascent
        // Except the last one, they are always the Ascent to Deep Stop (ADS) or Ascent to Surface (AS)

        xPos = anchorLineX + anchorLineThickness + markerLineLength;

        // i in order starting from 0
        // Always in that order but not always present
        // 0 AS  Ascent to Surface
        // 1 SS  Safety Stop
        // 2 DS  Deep Stop
        // 3 ADS Ascent to Deep Stop
        // 4 OOA Out Of Air Situation
        for (int i = 0; i < mDiveSegmentAscentList.size(); i++) {
            diveSegment = mDiveSegmentAscentList.get(i);
            //          Safety Stop
            //          3 min @ 15feet
            //          3368.5 psi
            //          Ascent @ 10 ft/min

            if (!diveSegment.getSegmentType().equals("SS") && !diveSegment.getSegmentType().equals("DS")) {
                continue;
            }

            // Ascent type
            switch (diveSegment.getSegmentType()) {
                case "SS":
                    text = mContext.getResources().getString(R.string.lbl_safety_stop);
                    break;
                case "DS":
                    text = mContext.getResources().getString(R.string.lbl_deep_stop);
                    break;
            }

            depthPercentd = diveSegment.getDepth() / mDiveSegmentDetail.getMaxDepth();
            depthPercentf = (float) depthPercentd;

            yPos = (diveHeight * depthPercentf) + surfaceLineY;
            canvas.drawText(text, xPos, yPos, mPaintText);

            // Followed immediately by a red marker line at the right of the Anchor line
            // Need to use the same yPos
            canvas.drawLine(anchorLineX + anchorLineThickness, yPos, anchorLineX + markerLineLength, yPos, mPaintLine);

            // Time and Depth
            text = MyFunctions.convertMinToString(diveSegment.getMinute());
            text += " " + mTimeUnit;
            text += " @ " + diveSegment.getDepth();
            text += " " + mDepthUnit;
            textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);

            yPos += textHeight;
            canvas.drawText(text, xPos, yPos, mPaintText);

            // psi
            text = String.valueOf(diveSegment.getCalcDecreasingPressure());
            text += " " + mPressureUnit;
            textHeight = MyFunctionsSegments.getTextHeightTaller(mPaintText);

            yPos += textHeight;
            canvas.drawText(text, xPos, yPos, mPaintText);

            // Ascent Rate
            text = mAscent + " @";
            // The Ascent Rate for the Safety Stop is stored in the AS (Ascent to Surface) segment
            text += " " + MyFunctionsSegments.getAscentRateSS(mDiveSegmentAscentList,i + 1);
            text += " " + mRateUnit;
            textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);

            yPos += textHeight;
            mPaintText.setColor(Color.RED);
            canvas.drawText(text, xPos, yPos, mPaintText);
            mPaintText.setColor(Color.BLACK);
        }

        // The LAST (in term of drawing) or FIRST (in term of ascending) set of ascent numbers (ADS) are located to the right of the Anchor line and above the Bottom line
        // No Marker line
        // No Time and Depth
        // Drawing from the bottom up

        // ADS is not present if the maximum depth is below deepStopDive e.g. 40 feet
        // ADS and ASS segments are not present in a shorten situation
        // Replace them with the OOA
        int i = MyFunctionsSegments.findAscentSegment(mDiveSegmentAscentList,"ADS");
        if (i == -1) {
            i = MyFunctionsSegments.findAscentSegment(mDiveSegmentAscentList,"ASS");
            if (i == -1) {
                i = MyFunctionsSegments.findAscentSegment(mDiveSegmentAscentList,"AS");
            }
        }

        diveSegment = mDiveSegmentAscentList.get(i);

        // Ascent Rate
        text = mAscent + " @";
        text += " " + diveSegment.getCalcAscentRate();
        text += " " + mRateUnit;

        yPos = (mHeight * bottomLineRatio) - bottomLineThickness - 1.0f;
        mPaintText.setColor(Color.RED);
        canvas.drawText(text, xPos, yPos, mPaintText);

        // psi
        text = String.valueOf(diveSegment.getCalcDecreasingPressure());
        text += " " + mPressureUnit;
        textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);

        yPos -= textHeight;
        canvas.drawText(text, xPos, yPos, mPaintText);
        mPaintText.setColor(Color.BLACK);

        // Ascent
        text = mAscent;
        textHeight = MyFunctionsSegments.getTextHeightTaller(mPaintText);

        yPos -= textHeight;
        canvas.drawText(text, xPos, yPos, mPaintText);

        // DEBUG:
        // Exploratory code
//        Paint paintLine; // To paint line
//        paintLine = new Paint();
//        paintLine.setColor(Color.GREEN);
//        paintLine.setStyle(Paint.Style.STROKE);
//        paintLine.setStrokeWidth(10);
//        paintLine.setAntiAlias(true);
//        // Draw an horizontal line between the flag and the buoy
//        // Small screen
//        canvas.drawLine(0, 105, 490, 105, mPaintLine);
//        // Large screen
//        canvas.drawLine(0, 144, 490, 144, paintLine);
//        // Draw an horizontal line at Surface
//        // Small screen
//        canvas.drawLine(0, 216, 490, 216, mPaintLine);
//        // Large screen
//        canvas.drawLine(0, 201, 326, 201, paintLine);
//        // Draw an horizontal line at Bottom
//        // Small screen
//        canvas.drawLine(0, 1261, 700, 1261, mPaintLine);
//        // Large screen
//        canvas.drawLine(0, 953, 700, 953, paintLine);
//        // Draw a vertical line at Anchor
//        // Small screen
//        canvas.drawLine(439.5f, 800, 439.5f, 1200, mPaintLine);
//        // Large screen
//        canvas.drawLine(535.5f, 650, 535.5f, 950, paintLine);
//        //Fill the view with Red to see the it's full size
//        canvas.drawColor(Color.RED);
    }

    private void setupPaint() {
        mPaintText = new Paint();
        mPaintText.setColor(Color.BLACK);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setAntiAlias(true);
        mPaintText.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.smalldp));
        mPaintText.setTextAlign(Paint.Align.LEFT);
        mPaintText.setLinearText(true);

        mPaintLine = new Paint();
        mPaintLine.setColor(Color.RED);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeWidth(10);
        mPaintLine.setAntiAlias(true);

        mTimeUnit = mContext.getResources().getString(R.string.lbl_minute);
        mStart = mContext.getResources().getString(R.string.lbl_start);
        mStop = mContext.getResources().getString(R.string.lbl_stop);
        mAscent = mContext.getResources().getString(R.string.lbl_ascent);
        mRunningTotals = mContext.getResources().getString(R.string.lbl_running_totals);
        mRunningAscent = mContext.getResources().getString(R.string.lbl_running_ascent);

        MyCalc myCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            myCalc = new MyCalcImperial(mContext);
        } else {
            myCalc = new MyCalcMetric(mContext);
        }
        mPressureUnit = myCalc.getPressureUnit();
        mRateUnit = myCalc.getRateUnit();
        mDepthUnit = myCalc.getDepthUnit();
        mVolumeUnit = myCalc.getVolumeUnit();
    }

    public void setDiveSegmentDetail (DiveSegmentDetail diveSegmentDetail) {mDiveSegmentDetail = diveSegmentDetail;}

    void setDiveSegmentAscent(ArrayList<DiveSegment> diveSegmentAscentList) {mDiveSegmentAscentList = diveSegmentAscentList;}
}
