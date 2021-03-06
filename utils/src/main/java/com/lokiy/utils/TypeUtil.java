/**
 * Copyright (C) 2016 Luki(liulongke@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 　　　　http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lokiy.utils;

import java.io.Serializable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

/**
 * TypeUtil
 * Created by Luki on 2016/8/10.
 * Version:1
 * modify from com.google.gson.internal.$Gson$Types
 */
public class TypeUtil {

	static final Type[] EMPTY_TYPE_ARRAY = new Type[]{};

	public static ParameterizedTypeImpl getParameterizedType(Type rawType, Type type) {
		return new ParameterizedTypeImpl(null, rawType, type);
	}

	/**
	 * Returns the type from super class's type parameter in {@link #canonicalize
	 * canonical form}.
	 * @param subclass class
	 * @return  type
	 */
	public static Type getSuperclassTypeParameter(Class<?> subclass) {
		Type superclass = subclass.getGenericSuperclass();
		if (superclass instanceof Class) {
			throw new RuntimeException("Missing type parameter.");
		}
		ParameterizedType parameterized = (ParameterizedType) superclass;
		return canonicalize(parameterized.getActualTypeArguments()[0]);
	}

	/**
	 * Returns a type that is functionally equal but not necessarily equal
	 * according to {@link Object#equals(Object) Object.equals()}. The returned
	 * type is {@link Serializable}.
	 */
	private static Type canonicalize(Type type) {
		if (type instanceof Class) {
			Class<?> c = (Class<?>) type;
			return c.isArray() ? new GenericArrayTypeImpl(canonicalize(c.getComponentType())) : c;

		} else if (type instanceof ParameterizedType) {
			ParameterizedType p = (ParameterizedType) type;
			return new ParameterizedTypeImpl(p.getOwnerType(), p.getRawType(), p.getActualTypeArguments());

		} else if (type instanceof GenericArrayType) {
			GenericArrayType g = (GenericArrayType) type;
			return new GenericArrayTypeImpl(g.getGenericComponentType());

		} else if (type instanceof WildcardType) {
			WildcardType w = (WildcardType) type;
			return new WildcardTypeImpl(w.getUpperBounds(), w.getLowerBounds());

		} else {
			// type is either serializable as-is or unsupported
			return type;
		}
	}

	private static String typeToString(Type type) {
		return type instanceof Class ? ((Class<?>) type).getName() : type.toString();
	}

	private static void checkNotPrimitive(Type type) {
		checkArgument(!(type instanceof Class<?>) || !((Class<?>) type).isPrimitive());
	}

	private static void checkArgument(boolean condition) {
		if (!condition) {
			throw new IllegalArgumentException();
		}
	}

	private static int hashCodeOrZero(Object o) {
		return o != null ? o.hashCode() : 0;
	}

	private static <T> T checkNotNull(T obj) {
		if (obj == null) {
			throw new NullPointerException();
		}
		return obj;
	}

	/**
	 * Returns true if {@code a} and {@code b} are equal.
	 */
	private static boolean equals(Type a, Type b) {
		if (a == b) {
			// also handles (a == null && b == null)
			return true;

		} else if (a instanceof Class) {
			// Class already specifies equals().
			return a.equals(b);

		} else if (a instanceof ParameterizedType) {
			if (!(b instanceof ParameterizedType)) {
				return false;
			}

			// TODO: save a .clone() call
			ParameterizedType pa = (ParameterizedType) a;
			ParameterizedType pb = (ParameterizedType) b;
			return equal(pa.getOwnerType(), pb.getOwnerType()) && pa.getRawType().equals(pb.getRawType()) && Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments());

		} else if (a instanceof GenericArrayType) {
			if (!(b instanceof GenericArrayType)) {
				return false;
			}

			GenericArrayType ga = (GenericArrayType) a;
			GenericArrayType gb = (GenericArrayType) b;
			return equals(ga.getGenericComponentType(), gb.getGenericComponentType());

		} else if (a instanceof WildcardType) {
			if (!(b instanceof WildcardType)) {
				return false;
			}

			WildcardType wa = (WildcardType) a;
			WildcardType wb = (WildcardType) b;
			return Arrays.equals(wa.getUpperBounds(), wb.getUpperBounds()) && Arrays.equals(wa.getLowerBounds(), wb.getLowerBounds());

		} else if (a instanceof TypeVariable) {
			if (!(b instanceof TypeVariable)) {
				return false;
			}
			TypeVariable<?> va = (TypeVariable<?>) a;
			TypeVariable<?> vb = (TypeVariable<?>) b;
			return va.getGenericDeclaration() == vb.getGenericDeclaration() && va.getName().equals(vb.getName());

		} else {
			// This isn't a type we support. Could be a generic array type, wildcard type, etc.
			return false;
		}
	}

	static boolean equal(Object a, Object b) {
		return a == b || (a != null && a.equals(b));
	}

	public static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {
		private static final long serialVersionUID = 0;
		private final Type ownerType;
		private final Type rawType;
		private final Type[] typeArguments;

		public ParameterizedTypeImpl(Type ownerType, Type rawType, Type... typeArguments) {
			// require an owner type if the raw type needs it
			if (rawType instanceof Class<?>) {
				Class<?> rawTypeAsClass = (Class<?>) rawType;
				boolean isStaticOrTopLevelClass = Modifier.isStatic(rawTypeAsClass.getModifiers()) || rawTypeAsClass.getEnclosingClass() == null;
				checkArgument(ownerType != null || isStaticOrTopLevelClass);
			}

			this.ownerType = ownerType == null ? null : canonicalize(ownerType);
			this.rawType = canonicalize(rawType);
			this.typeArguments = typeArguments.clone();
			for (int t = 0; t < this.typeArguments.length; t++) {
				checkNotNull(this.typeArguments[t]);
				checkNotPrimitive(this.typeArguments[t]);
				this.typeArguments[t] = canonicalize(this.typeArguments[t]);
			}
		}

		public Type[] getActualTypeArguments() {
			return typeArguments.clone();
		}

		public Type getOwnerType() {
			return ownerType;
		}

		public Type getRawType() {
			return rawType;
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof ParameterizedType && TypeUtil.equals(this, (ParameterizedType) other);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(typeArguments) ^ rawType.hashCode() ^ hashCodeOrZero(ownerType);
		}

		@Override
		public String toString() {
			StringBuilder stringBuilder = new StringBuilder(30 * (typeArguments.length + 1));
			stringBuilder.append(typeToString(rawType));

			if (typeArguments.length == 0) {
				return stringBuilder.toString();
			}

			stringBuilder.append("<").append(typeToString(typeArguments[0]));
			for (int i = 1; i < typeArguments.length; i++) {
				stringBuilder.append(", ").append(typeToString(typeArguments[i]));
			}
			return stringBuilder.append(">").toString();
		}


	}

	private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable {
		private static final long serialVersionUID = 0;
		private final Type componentType;

		public GenericArrayTypeImpl(Type componentType) {
			this.componentType = canonicalize(componentType);
		}

		public Type getGenericComponentType() {
			return componentType;
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof GenericArrayType && TypeUtil.equals(this, (GenericArrayType) o);
		}

		@Override
		public int hashCode() {
			return componentType.hashCode();
		}

		@Override
		public String toString() {
			return typeToString(componentType) + "[]";
		}


	}

	/**
	 * The WildcardType interface supports multiple upper bounds and multiple
	 * lower bounds. We only support what the Java 6 language needs - at most one
	 * bound. If a lower bound is set, the upper bound must be Object.class.
	 */
	private static final class WildcardTypeImpl implements WildcardType, Serializable {
		private static final long serialVersionUID = 0;
		private final Type upperBound;
		private final Type lowerBound;

		public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
			checkArgument(lowerBounds.length <= 1);
			checkArgument(upperBounds.length == 1);

			if (lowerBounds.length == 1) {
				checkNotNull(lowerBounds[0]);
				checkNotPrimitive(lowerBounds[0]);
				checkArgument(upperBounds[0] == Object.class);
				this.lowerBound = canonicalize(lowerBounds[0]);
				this.upperBound = Object.class;

			} else {
				checkNotNull(upperBounds[0]);
				checkNotPrimitive(upperBounds[0]);
				this.lowerBound = null;
				this.upperBound = canonicalize(upperBounds[0]);
			}
		}

		public Type[] getUpperBounds() {
			return new Type[]{upperBound};
		}

		public Type[] getLowerBounds() {
			return lowerBound != null ? new Type[]{lowerBound} : EMPTY_TYPE_ARRAY;
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof WildcardType && TypeUtil.equals(this, (WildcardType) other);
		}

		@Override
		public int hashCode() {
			// this equals Arrays.hashCode(getLowerBounds()) ^ Arrays.hashCode(getUpperBounds());
			return (lowerBound != null ? 31 + lowerBound.hashCode() : 1) ^ (31 + upperBound.hashCode());
		}

		@Override
		public String toString() {
			if (lowerBound != null) {
				return "? super " + typeToString(lowerBound);
			} else if (upperBound == Object.class) {
				return "?";
			} else {
				return "? extends " + typeToString(upperBound);
			}
		}


	}
}
