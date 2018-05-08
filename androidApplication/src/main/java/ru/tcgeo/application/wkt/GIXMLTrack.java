package ru.tcgeo.application.wkt;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ru.tcgeo.application.App;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIEncoding;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.GIVectorStyle;
import ru.tcgeo.application.gilib.planimetry.Edge;
import ru.tcgeo.application.gilib.planimetry.Vertex;
import ru.tcgeo.application.utils.MapUtils;

public class GIXMLTrack extends GI_WktGeometry {

	public int m_TrackID;
	public String m_file;
	public String m_name_wo_extention;
	public ArrayList<GI_WktGeometry> m_points;
	Path m_path;
	/**/
//	FileOutputStream m_output;
	BufferedWriter m_writer;

	public GIXMLTrack() {
		m_type = GIWKTGeometryType.TRACK;
		m_status = GIWKTGeometryStatus.NEW;
		m_points = new ArrayList<GI_WktGeometry>();
		m_TrackID = -1;
		m_path = new Path();
	}

	@Override
	public String toWKT() {
		File f = new File(m_file);
		String res = f.getName();
		return "FILE\"" + res + "\"";
	}

	@Override
	public void Draw(Canvas canvas, GIBounds area, float scale, GIVectorStyle style/*android.graphics.Paint paint*/)
	{

		//todo
		if (App.Instance().getCurrentTrack() == this) {
			return;
		}
		if(m_points.size() > 2)
		{
			m_path.reset();
			int counter = 0;
			while(counter < m_points.size() - 1)
			{

				float pixel_weight = (float)((area.right() - area.left())/canvas.getWidth());
				int current_index = counter + 1;
				while(current_index < m_points.size())
				{
					double distance = Math.hypot(((GI_WktPoint)m_points.get(current_index)).m_lon_in_map_projection - ((GI_WktPoint)m_points.get(counter)).m_lon_in_map_projection, ((GI_WktPoint)m_points.get(current_index)).m_lat_in_map_projection - ((GI_WktPoint)m_points.get(counter)).m_lat_in_map_projection);
					if(distance > pixel_weight*5)
					{
						break;
					}
					if(current_index >=  m_points.size() - 1)
					{
						break;
					}
					current_index = current_index + 1;
				}
//				//TODO
//				PointF point_current = ((GI_WktPoint)m_points.get(current_index)).MapToScreen(canvas, area);
//				path.lineTo( point_current.x, point_current.y);
//				canvas.drawPath(path, paint);
				//TODO
				if(area.ContainsPoint(((GI_WktPoint)m_points.get(counter)).LonLat()) || area.ContainsPoint(((GI_WktPoint)m_points.get(current_index)).LonLat()))
				{

					PointF point_prev = ((GI_WktPoint)m_points.get(counter)).MapToScreen(canvas, area);
					PointF point_current = ((GI_WktPoint)m_points.get(current_index)).MapToScreen(canvas, area);
					canvas.drawLine(point_prev.x, point_prev.y, point_current.x, point_current.y, style.m_paint_pen);

				}
				counter = current_index;
				if(Thread.interrupted())
				{
					return;
				}

			}

		}
	}

	@Override
	public void Paint(Canvas canvas, GIBounds area, GIVectorStyle style) {
		if(m_points.size() > 1) //2
		{
//			GIBounds area = App.Instance().getMap().Bounds();
			int[] offset = { 0, 0 };
			App.Instance().getMap().getLocationOnScreen(offset);

			if (this != App.Instance().getCurrentTrack())
			{
				((GI_WktPoint)m_points.get(0)).TrackPaint(canvas, style);
			}
			for(int i = m_points.size() - 1; i > 0; i--)
			{
				PointF point_prev = ((GI_WktPoint)m_points.get(i-1)).MapToScreen(canvas, area);
				PointF point_current = ((GI_WktPoint)m_points.get(i)).MapToScreen(canvas, area);
				//canvas.drawLine(point_prev.x - offset[0], point_prev.y - offset[1], point_current.x - offset[0], point_current.y - offset[1], style.m_paint_pen);
				canvas.drawLine(point_prev.x, point_prev.y, point_current.x, point_current.y, style.m_paint_pen);

				if (this != App.Instance().getCurrentTrack())
				{
					((GI_WktPoint)m_points.get(i)).TrackPaint(canvas, style);
				}

				if(Thread.interrupted())
				{
					return;
				}
			}
		}

	}

	@Override
	public boolean IsEmpty() {
		return (m_points.size() == 0);
	}

	@Override
	public boolean isTouch(GIBounds point)
	{
		boolean all_in_bounds = m_points.size() > 0;
		for(GI_WktGeometry g : m_points)
		{
			GI_WktPoint vertex = (GI_WktPoint)g;
			if( !point.ContainsPoint(new GILonLat(vertex.m_lon, vertex.m_lat)))
			{
				all_in_bounds = false;
				break;
			}
		}
		if(all_in_bounds)
		{
			return true;
		}
		RectF rect = new RectF((float)point.left(), (float)point.top(), (float)point.right(), (float)point.bottom());
		ArrayList<Vertex> geom = new ArrayList<Vertex>();
		geom.add(new Vertex(rect.left, rect.bottom));
		geom.add(new Vertex(rect.right, rect.bottom));
		geom.add(new Vertex(rect.right, rect.top));
		geom.add(new Vertex(rect.left, rect.top));
		geom.add(new Vertex(rect.left, rect.bottom));

		for(int i = 0; i < m_points.size() - 1; i++)
		{
			GILonLat lonlat_start = new GILonLat(((GI_WktPoint)m_points.get(i)).m_lon, ((GI_WktPoint)m_points.get(i)).m_lat);
			GILonLat lonlat_end  = new GILonLat(((GI_WktPoint)m_points.get(i+1)).m_lon, ((GI_WktPoint)m_points.get(i+1)).m_lat);
			GILonLat lonlat_proj_start = GIProjection.ReprojectLonLat(lonlat_start, GIProjection.WGS84(), GIProjection.WorldMercator());
			GILonLat lonlat_proj_end = GIProjection.ReprojectLonLat(lonlat_end, GIProjection.WGS84(), GIProjection.WorldMercator());
			PointF pointf_start = new PointF((float)lonlat_proj_start.lon(), (float)lonlat_proj_start.lat());
			PointF pointf_end = new PointF((float)lonlat_proj_end.lon(), (float)lonlat_proj_end.lat());
			Edge current = new Edge(pointf_start, pointf_end);
			if(current.SectionByPolygon(geom) != null)
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean Create(String folder, String name, GIVectorStyle style, GIEncoding encoding)
	{
		m_name_wo_extention = name;

		m_file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folder + File.separator + name  + ".track";

		return CreateOutput(folder, name);

	}

	//TODO test it
	public void AddPoint(GI_WktPoint point, float accurancy)
	{
		if(accurancy > 60){
			return;
		}

		boolean res = true;
		if(m_points.size() > 1)
		{
			double distance = MapUtils.GetDistance(((GI_WktPoint) m_points.get(m_points.size() - 1)).LonLat(), point.LonLat());
			res = distance > accurancy*1.5f;
		}
		//TODO uncomment
		if(res)
		{
			point.m_ID = m_points.size();
			m_points.add(point);
			AppendToFile(point.toWKT());
		}
	}

	@Override
	public void Delete()
	{
		File f = new File(m_file);
		if(f.exists())
		{
			f.delete();
		}
		else
		{
			Log.d("LOG_TAG", f.getAbsolutePath() + "couldnt be found!");
		}

	}

	public boolean CreateOutput(String folder, String name)
	{
		File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folder);
		if(!dir.exists()){
			dir.mkdir();
		}
		boolean res = false;
		try 
		{
			m_writer = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folder + File.separator + name  + ".track", true));
			res = true;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			res = false;
		}
		return res;
	}
	public void AppendToFile(String string)
	{
		if(m_writer != null)
		{
			try
			{
				m_writer.write(string);
				m_writer.newLine();
				m_writer.flush();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

	public void StopTrack()
	{
		if(m_writer != null)
		{
			try
			{
				m_writer.flush();
				m_writer.close();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public String SerializedGeometry() 
	{
		return m_file;
	}

//	private void load(){
//		try
//		{
//			BufferedReader reader = new BufferedReader(new FileReader(m_file));
//			String line = "";
//			while((line = reader.readLine()) != null)
//			{
//				GI_WktGeometry point = GIWKTParser.CreateGeometryFromWKT(line);
//				m_points.add((GI_WktPoint) point);
//			}
//			reader.close();
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//	}

	@Override
	public void free(){
//		for(GI_WktGeometry geometry : m_points){
//			geometry.free();
//		}
//		m_points.clear();
	}
}