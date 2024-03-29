/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2008 Erwin Coumans  http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.collision.broadphase;

import java.util.Comparator;

/**
 * BroadphasePair class contains a pair of AABB-overlapping objects.
 * {@link Dispatcher} can search a {@link CollisionAlgorithm} that performs
 * exact/narrowphase collision detection on the actual collision shapes.
 *
 * @author jezek2
 */
public class BroadphasePair {

	public BroadphaseProxy pProxy0;
	public BroadphaseProxy pProxy1;
	public CollisionAlgorithm algorithm;
	public Object userInfo;

	public BroadphasePair() {
	}

	public BroadphasePair(BroadphaseProxy pProxy0, BroadphaseProxy pProxy1) {
		this.pProxy0 = pProxy0;
		this.pProxy1 = pProxy1;
		this.algorithm = null;
		this.userInfo = null;
	}
	
	public void set(BroadphasePair p) {
		pProxy0 = p.pProxy0;
		pProxy1 = p.pProxy1;
		algorithm = p.algorithm;
		userInfo = p.userInfo;
	}
	
	public boolean equals(BroadphasePair p) {
		return pProxy0 == p.pProxy0 && pProxy1 == p.pProxy1;
	}
	
	public static final Comparator<BroadphasePair> broadphasePairSortPredicate = new Comparator<BroadphasePair>() {
		public int compare(BroadphasePair a, BroadphasePair b) {
			// JAVA TODO:
			boolean result = a.pProxy0.getUid() > b.pProxy0.getUid() ||
					(a.pProxy0.getUid() == b.pProxy0.getUid() && a.pProxy1.getUid() > b.pProxy1.getUid()) ||
					(a.pProxy0.getUid() == b.pProxy0.getUid() && a.pProxy1.getUid() == b.pProxy1.getUid() /*&& a.algorithm > b.m_algorithm*/);
			return result? -1 : 1;
		}
	};
	
}
\begin{pmatrix}-14.85364&-15.4069&0\end{pmatrix}