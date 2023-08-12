package ca.myairbuddyandi;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Michel on 2017-10-10.
 * Holds all of the logic for the RockbottomView class
 */

public class RockbottomView extends View {

    // Static
    private static final String LOG_TAG = "RockbottomView";

    // Public

    // Protected

    // Private
    private int mDescentRate;
    private int mHeight; // Total height of the graphic
    private int mWidth; // Total width of the graphic
    private ArrayList<DiveSegment> mDiveSegmentDescentList = new ArrayList<>(); // Contains the Descent numbers
    private ArrayList<DiveSegment> mDiveSegmentAscentList = new ArrayList<>(); // Contains the Ascent numbers
    private final Context mContext;
    private DiveSegmentDetail mDiveSegmentDetail; // Contains the general milestones numbers
    private Paint mPaintText; // To paint text
    private Paint mPaintLine; // To paint line
    private final Rect mBounds = new Rect();
    private String mAscent;
    private String mBottomTime;
    private String mBubbleCheck;
    private String mDepthUnit;
    private String mDescent;
    private String mPressureUnit;
    private String mRateUnit;
    private String mRunningAscent;
    private String mRunningDescent;
    private String mRunningTotals;
    private String mStart;
    private String mStop;
    private String mTimeUnit;
    private String mTurnaround;
    private String mVolumeUnit;

    // End of variables

    // Public constructor
    public RockbottomView(Context context) {
        super(context);
        mContext = context;
        setupPaint();
        Log.d(LOG_TAG, "constructor done");
    }

    // Public constructor
    public RockbottomView(Context context, @Nullable AttributeSet attrs) {
        // Normal point of entry
        super(context, attrs);
        mContext = context;
        setupPaint();
        Log.d(LOG_TAG, "constructor done");
    }

    // Public constructor
    public RockbottomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setupPaint();
        Log.d(LOG_TAG, "constructor done");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        mHeight = View.MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        double depthPercentd;

        // To calculate the ratios:
        // - Get the Width and the Height from the onMeasure() function above
        // - Uncomment the code in the Exploratory section below
        // As an example:
        // - Take a x coordinate in the exploratory section below and divide it by the width of the screen
        // - Take a y coordinate in the exploratory section below and divide it by the height of the screen
        float anchorLineRatio; // Ratio to find position of Anchor line from left margin
        float bottomLineRatio; // Ratio to find the position of the Bottom line from top margin
        float surfaceLineRatio; // Ratio to find the position of the Surface line from top margin
        float turnaroundLineRatio; // Ratio to find the position of the Turnaround line from left margin
        float runningTotalsLineRatio; // Ratio to find the position of the Running Totals line from top margin

        float runningTotalsLineRatioSmall = 0.0719671007539411f; // Ratio to find the position of the Running Totals line from top margin 105 / 1459
        float surfaceLineRatioSmall = 0.1480466072652502f; // Ratio to find the position of the Surface line from top margin 216 / 1459
        float bottomLineRatioSmall = 0.864290610006854f; // Ratio to find the position of the Bottom line from top margin 1261 / 1459
        float anchorLineRatioSmall = 0.4069444444444444f; // Ratio to find position of Anchor line from left margin 439.5 / 1080
        float turnaroundLineRatioSmall = 0.9333333333333333f; // Ratio to find the position of the Turnaround line from left margin 1008 / 1080

        float runningTotalsLineRatioLarge = 0.1190243902439024f; // Ratio to find the position of the Running Totals line from top margin 122 / 1025
        float surfaceLineRatioLarge = 0.1863414634146341f; // Ratio to find the position of the Surface line from top margin 191 / 1025
        float bottomLineRatioLarge = 0.864290610006854f; // Ratio to find the position of the Bottom line from top margin 953 / 1025
        float anchorLineRatioLarge = 0.4069444444444444f; // Ratio to find position of Anchor line from left margin 325.5 / 800
        float turnaroundLineRatioLarge = 0.9333333333333333f; // Ratio to find the position of the Turnaround line from left margin 747 / 800

        float anchorLineX; // Scaled position of the Anchor line
        float bottomLineY; // Scaled position of the Bottom line
        float surfaceLineY; // Scaled position of the Surface line
        float anchorLineThickness = 1.0f; // Thickness of the Anchor line
        float bottomLineThickness = 5.0f; // Thickness of the Bottom line
        float xPos; // x position to start the actual drawing of text
        float xPosFarLeft = 5.0f; // x position at the far left of the screen
        float yPos;// y position to start the actual drawing of text
        float textHeight;
        float diveHeight;
        float depthPercentf;
        int markerLineLength = 25; // Length of the marker on either the left or right side of the Anchor line
        String text;

        if (mDiveSegmentDetail == null) {
            return;
        }

        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==  Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            // on a large screen device
            anchorLineRatio = anchorLineRatioLarge;
            bottomLineRatio = bottomLineRatioLarge;
            surfaceLineRatio = surfaceLineRatioLarge;
            turnaroundLineRatio = turnaroundLineRatioLarge;
            runningTotalsLineRatio = runningTotalsLineRatioLarge;
        } else {
            // On a small screen device
            anchorLineRatio = anchorLineRatioSmall;
            bottomLineRatio = bottomLineRatioSmall;
            surfaceLineRatio = surfaceLineRatioSmall;
            turnaroundLineRatio = turnaroundLineRatioSmall;
            runningTotalsLineRatio = runningTotalsLineRatioSmall;
        }

        anchorLineX = mWidth * anchorLineRatio;
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

        // Running Descent Time
        // Located to the left of the anchor line
        // Between the flag and the buoy
        // Below the Running time and volume

        text = mRunningDescent;
        text += ": " + MyFunctions.convertMinToString(mDiveSegmentDetail.getRunningDescentTime());
        text += " " + mTimeUnit;
        text += ": " + MyFunctions.roundUp(mDiveSegmentDetail.getRunningDescentPressure(), 1);
        text += " " + mPressureUnit;
        text += ", " + MyFunctions.roundUp(mDiveSegmentDetail.getRunningDescentVolume(), 1);
        text += " " + mVolumeUnit;
        textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);

        yPos = yPos + textHeight + 10;

        canvas.drawText(text, xPosFarLeft, yPos, mPaintText);

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

        canvas.drawText(text, anchorLineX + anchorLineThickness + markerLineLength, yPos, mPaintText);

        // Start
        // Located at the top left
        // Example: Start 3442.0 psi
        //          Descent @ 30 ft/min
        text = mStart;
        text += " " + mDiveSegmentDetail.getBeginningPressure();
        text += " " + mPressureUnit;
        textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);

        yPos = (mHeight * surfaceLineRatio) + textHeight;
        canvas.drawText(text, xPosFarLeft, yPos, mPaintText);

        // Start Descent rate
        text = mDescent;
        text += " " + mDescentRate;
        text += " " + mRateUnit;
        textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);

        yPos += textHeight;
        mPaintText.setColor(Color.RED);
        canvas.drawText(text, xPosFarLeft, yPos, mPaintText);
        mPaintText.setColor(Color.BLACK);

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

        // NOTE: The Descent set of numbers
        // Drawing from surface to bottom
        // There should be only one descent Bottom Time and that should be a Bubble Check
        DiveSegment diveSegment;
        for (int i = 0; i < mDiveSegmentDescentList.size(); i++) {
            diveSegment = mDiveSegmentDescentList.get(i);

            // Example: Bubble Check
            //          10 min @ 30 feet
            //          3368.5 psi
            //          Descent @ 30 ft/min

            if (diveSegment.getSegmentType().equals("BC")) {

                // Bottom Time (intermediate)
                text = mBubbleCheck;

                depthPercentd = diveSegment.getDepth() / mDiveSegmentDetail.getMaxDepth();
                depthPercentf = (float) depthPercentd;

                yPos = (diveHeight * depthPercentf) + surfaceLineY;
                canvas.drawText(text, xPosFarLeft, yPos, mPaintText);

                // Followed immediately by a red marker line at the left of the Anchor line
                // Need to use the same yPos
                canvas.drawLine(anchorLineX - markerLineLength - anchorLineThickness, yPos, anchorLineX, yPos, mPaintLine);

                // Time and Depth
                text = MyFunctions.convertMinToString(diveSegment.getMinute());
                text += " " + mTimeUnit;
                text += " @ " + diveSegment.getDepth();
                text += " " + mDepthUnit;
                textHeight = MyFunctionsSegments.getTextHeight(mPaintText, mBounds, text);

                yPos += textHeight;
                canvas.drawText(text, xPosFarLeft, yPos, mPaintText);

                // psi
                text = String.valueOf(diveSegment.getCalcDecreasingPressure());
                text += " " + mPressureUnit;
                textHeight = MyFunctionsSegments.getTextHeightTaller(mPaintText);

                yPos += textHeight;
                canvas.drawText(text, xPosFarLeft, yPos, mPaintText);

                // Descent Rate
                text = mDescent;
                text += " " + mDescentRate;
                text += " " + mRateUnit;
                textHeight = MyFunctionsSegments.getTextHeight(mPaintText, mBounds, text);

                yPos += textHeight;
                mPaintText.setColor(Color.RED);
                canvas.drawText(text, xPosFarLeft, yPos, mPaintText);
                mPaintText.setColor(Color.BLACK);
            }
        }

        // NOTE: Draw the Horizontal Bottom Line

        // In the Ascent List:
        // Position     Segment
        //  0           BT
        //  1           TA
        //  2           BT
        //  3           OOA

        // The first BOTTOM TIME (BT) (Forward) is located to the right of the Anchor line and below the Bottom line
        // No Marker line
        // No Descend rate
        diveSegment = mDiveSegmentAscentList.get(0);

        // Bottom Time
        text = mBottomTime;
        textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);

        xPos = anchorLineX + anchorLineThickness;
        yPos = (mHeight * bottomLineRatio) + bottomLineThickness + textHeight;
        canvas.drawText(text, xPos, yPos, mPaintText);

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

        // The Out of Air Situation (OOA)is located to the left of the Anchor line and below the Bottom line
        // No Marker line
        // No Descend rate
        // The OOA segment is always the fourth one (Index 3) in the list
        diveSegment = mDiveSegmentAscentList.get(3);
        text = mContext.getResources().getString(R.string.lbl_out_of_air);
        textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);
        yPos = (mHeight * bottomLineRatio) + bottomLineThickness + textHeight;
        canvas.drawText(text, xPosFarLeft, yPos, mPaintText);

        // Time and Depth
        text = MyFunctions.convertMinToString(diveSegment.getMinute());
        text += " " + mTimeUnit;
        text += " @ " + diveSegment.getDepth();
        text += " " + mDepthUnit;
        textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);

        yPos += textHeight;
        canvas.drawText(text, xPosFarLeft, yPos, mPaintText);

        // psi
        text = String.valueOf(diveSegment.getAirConsumptionPressure());
        text += " " + mPressureUnit;
        textHeight = MyFunctionsSegments.getTextHeightTaller(mPaintText);

        yPos += textHeight;
        canvas.drawText(text, xPosFarLeft, yPos, mPaintText);

        // The TURNAROUND is located to the far left and below the Anchor line
        // No Marker line
        // No Descend rate
        // The Turnaround segment is always the second one (Index 1) in the list
        diveSegment = mDiveSegmentAscentList.get(1);

        // Turnaround
        text = mTurnaround;
        textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);

        xPos = (mWidth * turnaroundLineRatio) - mPaintText.measureText(text);
        yPos = (mHeight * bottomLineRatio) + bottomLineThickness + textHeight;
        canvas.drawText(text, xPos, yPos, mPaintText);

        // Time and Depth
        text = MyFunctions.convertMinToString(MyConstants.ONE_D);
        text += " " + mTimeUnit;
        text += " @ " + diveSegment.getDepth();
        text += " " + mDepthUnit;
        textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);

        yPos += textHeight;
        canvas.drawText(text, xPos, yPos, mPaintText);

        // psi
        text = String.valueOf(diveSegment.getCalcDecreasingPressure());
        text += " " + mPressureUnit;
        mPaintText.setColor(Color.RED);
        textHeight = MyFunctionsSegments.getTextHeightTaller(mPaintText);

        yPos += textHeight;
        canvas.drawText(text, xPos, yPos, mPaintText);

        // The second BOTTOM TIME (BT) (Back) is located at the far right and above the Bottom line
        // No Marker line
        // No Descend rate
        // Drawing from the bottom up
        diveSegment = mDiveSegmentAscentList.get(2);

        // Calculate the X position based on the longest string
        text = MyFunctions.convertMinToString(diveSegment.getMinute());
        text += " " + mTimeUnit;
        text += " @ " + diveSegment.getDepth();
        text += " " + mDepthUnit;
        xPos = (mWidth * turnaroundLineRatio) - mPaintText.measureText(text);
        // Make sure to leave ample room between the Bottom line and the psi for the Ascent Rate of the Ascent set to show entirely without any overlap
        // No need to use resource, it is used as a template to measure the yPos
        yPos = (mHeight * bottomLineRatio) - bottomLineThickness - MyFunctionsSegments.getTextHeight(mPaintText,mBounds,"Ascent @ 30 feet/min");

        // psi
        text = String.valueOf(diveSegment.getCalcDecreasingPressure());
        text += " " + mPressureUnit;

//        mPaintText.setColor(Color.RED);
        canvas.drawText(text, xPos, yPos, mPaintText);
        mPaintText.setColor(Color.BLACK);

        // Time and Depth
        text = MyFunctions.convertMinToString(diveSegment.getMinute());
        text += " " + mTimeUnit;
        text += " @ " + diveSegment.getDepth();
        text += " " + mDepthUnit;
        textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text);

        yPos -= textHeight;
        canvas.drawText(text, xPos, yPos, mPaintText);

        // Bottom Time
        text = mBottomTime;
        textHeight = MyFunctionsSegments.getTextHeight(mPaintText,mBounds,text) + 10;

        yPos -= textHeight;
        canvas.drawText(text, xPos, yPos, mPaintText);

        // NOTE: The Ascent set of numbers
        // Drawing from the bottom to the surface, from after the deepest Bottom Time (BT) up to the surface
        // Except the last one, they are always the Ascent to Deep Stop (ADS) or Ascent to Surface (AS)

        xPos = anchorLineX + anchorLineThickness + markerLineLength;

        // i in order starting from 3 because the 3 first occurrences have already been drawn
        // Always in that order but not always present

        //  0 BT  Bottom Time. Always present. Already drawn above
        //  1 TA  Turnaround. Always present. Already drawn above
        //  2 BT  Bottom Time. Always present. Already drawn above
        //  3 OOA Out Of Air Situation
        //  4 AS  Ascend to second deepest Bottom Time. If present
        //  5 BT  Second deepest Bottom Time. If present
        //  6 AS  Ascend to third deepest Bottom Time. If present
        //  7 BT  Third deepest Bottom Time. If present
        //  ...
        //  8 ADS Ascend to Deep Stop. If present
        //  9 DS  Deep Stop. If present
        // 10 ASS Ascent toSafety Stop. If present
        // 11 SS  Ascent to Safety Stop. If present
        // 12 AS  Ascent to Surface

        for (int i = 3; i < mDiveSegmentAscentList.size(); i++) {
            diveSegment = mDiveSegmentAscentList.get(i);
            //          Safety Stop
            //          3 min @ 15feet
            //          3368.5 psi
            //          Ascent @ 10 ft/min

            if (!diveSegment.getSegmentType().equals("SS") && !diveSegment.getSegmentType().equals("DS") && !diveSegment.getSegmentType().equals("BT")) {
                continue;
            }

            // Ascent type
            switch (diveSegment.getSegmentType()) {
                case "BT":
                    text = mContext.getResources().getString(R.string.lbl_bottom_time);
                    break;
                case "SS":
                    text = mContext.getResources().getString(R.string.lbl_safety_stop);
                    break;
                case "DS":
                    text = mContext.getResources().getString(R.string.lbl_deep_stop);
                    break;
            }

            // If Bottom Time (BT), draw on the left side of the anchor line
            // All other on the right side of the anchor line
            // The xPos position changes
            // The yPos position stays the same
            if (diveSegment.getSegmentType().equals("BT")) {
                xPos = xPosFarLeft;
            } else {
                xPos = anchorLineX + anchorLineThickness + markerLineLength;
            }

            depthPercentd = diveSegment.getDepth() / mDiveSegmentDetail.getMaxDepth();
            depthPercentf = (float) depthPercentd;

            yPos = (diveHeight * depthPercentf) + surfaceLineY;
            canvas.drawText(text, xPos, yPos, mPaintText);

            // Followed immediately by a red marker line at the right of the Anchor line
            // Need to use the same yPos
            canvas.drawLine(anchorLineX + anchorLineThickness, yPos, anchorLineX + markerLineLength - anchorLineThickness, yPos, mPaintLine);

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
//        mPaintText.setColor(Color.RED);
        canvas.drawText(text, xPos, yPos, mPaintText);
        mPaintText.setColor(Color.BLACK);

        // Ascent
        text = mAscent;
        textHeight = MyFunctionsSegments.getTextHeightTaller(mPaintText);

        yPos -= textHeight;
//        mPaintText.setColor(Color.RED);
        canvas.drawText(text, xPos, yPos, mPaintText);
//        mPaintText.setColor(Color.BLACK);

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
//        canvas.drawLine(325.5f, 650, 325.5f, 950, paintLine);
//        // Draw a vertical line at Turnaround
//        // Small screen
//        canvas.drawLine(1008f, 800, 1008f, 1245, mPaintLine);
//        // Large screen
//        canvas.drawLine(747f, 650, 747f, 935, paintLine);
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
        mBottomTime = mContext.getResources().getString(R.string.lbl_bottom_time);
        mBubbleCheck = mContext.getResources().getString(R.string.lbl_bubble_check);
        mTurnaround = mContext.getResources().getString(R.string.lbl_turnaround);
        mDescent = mContext.getResources().getString(R.string.lbl_descent) + " @";
        mAscent = mContext.getResources().getString(R.string.lbl_ascent);
        mRunningTotals = mContext.getResources().getString(R.string.lbl_running_totals);
        mRunningDescent = mContext.getResources().getString(R.string.lbl_running_descent);
        mRunningAscent = mContext.getResources().getString(R.string.lbl_running_ascent);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mDescentRate = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.DESCENT_RATE, "30"))));
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

    void setDiveSegmentDescent(ArrayList<DiveSegment> diveSegmentDescentList) {mDiveSegmentDescentList = diveSegmentDescentList;}

    void setDiveSegmentAscent(ArrayList<DiveSegment> diveSegmentAscentList) {mDiveSegmentAscentList = diveSegmentAscentList;}
}
