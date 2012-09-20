package org.softlang.company.location;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class CompanyOverlay extends Overlay {

	private GeoPoint nwGeo;
	private GeoPoint neGeo;
	private GeoPoint swGeo;	
	private GeoPoint seGeo;
	
	public CompanyOverlay(double nwLatitude, double nwLongitude, double seLatitude, double seLongitude){
		nwGeo = new GeoPoint((int) (1E6 * nwLatitude), (int) (1E6 * nwLongitude));
		neGeo = new GeoPoint((int) (1E6 * nwLatitude), (int) (1E6 * seLongitude));
		swGeo = new GeoPoint((int) (1E6 * seLatitude), (int) (1E6 * nwLongitude));
		seGeo = new GeoPoint((int) (1E6 * seLatitude), (int) (1E6 * seLongitude));		
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		
		Projection projection;
		Paint mPaint = new Paint();
		projection = mapView.getProjection();
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);

        Point nwPoint = new Point();
        Point nePoint = new Point();
        Point sePoint = new Point();
        Point swPoint = new Point();
        Path topPath = new Path();
        Path bottomPath = new Path();
        Path leftPath = new Path();
        Path rightPath = new Path();

        projection.toPixels(nwGeo, nwPoint);
        projection.toPixels(neGeo, nePoint);
        projection.toPixels(seGeo, sePoint);
        projection.toPixels(swGeo, swPoint);

        topPath.moveTo(nwPoint.x, nwPoint.y);
        topPath.lineTo(nePoint.x, nePoint.y);
        canvas.drawPath(topPath, mPaint);
        
        bottomPath.moveTo(swPoint.x, swPoint.y);
        bottomPath.lineTo(sePoint.x, sePoint.y);
        canvas.drawPath(bottomPath, mPaint);

        leftPath.moveTo(nwPoint.x, nwPoint.y);
        leftPath.lineTo(swPoint.x, swPoint.y);
        canvas.drawPath(leftPath, mPaint);
		
        rightPath.moveTo(nePoint.x, nePoint.y);
        rightPath.lineTo(sePoint.x, sePoint.y);
        canvas.drawPath(rightPath, mPaint);
	}

	
}
