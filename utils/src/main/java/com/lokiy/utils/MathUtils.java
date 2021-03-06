/**
 * Copyright (C) 2014 Luki(liulongke@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lokiy.utils;

import android.graphics.PointF;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static java.lang.Math.PI;

public class MathUtils {
	private final static double R = 6371229; // R

	public static boolean pointInPolygon(PointF[] polygon, PointF p) {
		int i;
		int j = polygon.length - 1;
		boolean oddNodes = false;

		for (i = 0; i < polygon.length; i++) {
			float y = polygon[i].y;
			float y2 = polygon[j].y;
			float x = polygon[i].x;
			float x2 = polygon[j].x;
			float py = p.y;
			float px = p.x;
			if ((y < py && y2 >= py || y2 < py && y >= py) && (x <= px || x2 <= px)) {
				oddNodes ^= (x + (py - y) / (y2 - y) * (x2 - x) < px);
			}
			j = i;
		}
		return oddNodes;
	}

	/**
	 *
	 * @param lat1 lat1
	 * @param longt1 longt1
	 * @param lat2 lat2
	 * @param longt2 longt2
	 * @return distance of two points
	 * @throws Exception Exception
	 */
	public static double distance(double lat1, double longt1, double lat2, double longt2) throws Exception {
		double x, y, distance;
		x = (longt2 - longt1) * PI * R * Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;
		y = (lat2 - lat1) * PI * R / 180;
		distance = Math.hypot(x, y) / 1000;
		return Double.valueOf(new DecimalFormat("0.00").format(distance));
	}

	public static boolean isZeroPrice(double d) {
		return d < 0.01;
	}
	public static BigDecimal add(Number a, Number b) {
		return add(String.valueOf(a), String.valueOf(b));
	}

	public static BigDecimal add(String a, String b) {
		return new BigDecimal(a).add(new BigDecimal(b));
	}

	public static BigDecimal sub(Number a, Number b) {
		return sub(String.valueOf(a), String.valueOf(b));
	}

	public static BigDecimal sub(String a, String b) {
		return new BigDecimal(a).subtract(new BigDecimal(b));
	}

	public static BigDecimal div(Number a, Number b) {
		return div(String.valueOf(a), String.valueOf(b));
	}

	public static BigDecimal div(String a, String b) {
		return new BigDecimal(a).divide(new BigDecimal(b), 4, BigDecimal.ROUND_DOWN);
	}

	public static BigDecimal multiply(Number a, Number b) {
		return multiply(String.valueOf(a), String.valueOf(b));
	}

	public static BigDecimal multiply(String a, String b) {
		return new BigDecimal(a).multiply(new BigDecimal(b));
	}

	public static class Helper {
		private BigDecimal a;

		public Helper(Number a) {
			this(String.valueOf(a));
		}

		public Helper(String a) {
			this.a = new BigDecimal(a);
		}

		public Helper add(Number b) {
			this.add(String.valueOf(b));
			return this;
		}

		public Helper add(String b) {
			a = MathUtils.add(a, new BigDecimal(b));
			return this;
		}

		public Helper sub(Number b) {
			this.sub(String.valueOf(b));
			return this;
		}

		public Helper sub(String b) {
			a = MathUtils.sub(a, new BigDecimal(b));
			return this;
		}

		public Helper div(Number b) {
			this.div(String.valueOf(b));
			return this;
		}

		public Helper div(String b) {
			a = MathUtils.div(a, new BigDecimal(b));
			return this;
		}

		public Helper multiply(Number b) {
			this.multiply(String.valueOf(b));
			return this;
		}

		public Helper multiply(String b) {
			a = MathUtils.multiply(a, new BigDecimal(b));
			return this;
		}

		public BigDecimal result() {
			return a;
		}
	}


}
