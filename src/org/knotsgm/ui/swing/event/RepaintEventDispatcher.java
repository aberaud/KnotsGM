package org.knotsgm.ui.swing.event;

public interface RepaintEventDispatcher
{
	public void addRepaintEventListener(RepaintListener listener);
	public void removeRepaintEventListener(RepaintListener listener);
}
