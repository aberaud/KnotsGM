/* 
 * Copyright (C) 2008-2009 Adrien Béraud <adrienberaud@gmail.com>
 * 
 * This file is a part of KnotsGM, The Knots Graphical Manipulator
 * 
 * KnotsGM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.knotsgm.ui.swing.event;

import java.util.EventListener;

public interface KnotManagerEventListener extends EventListener
{
	public void drawingString(KnotManagerEvent evt);
	public void stringDrawn(KnotManagerEvent evt);
	//public void startChangeIntersections(KnotManagerEvent evt);
	//public void stopChangeIntersections(KnotManagerEvent evt);
}
