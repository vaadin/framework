package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ITime extends FlowPanel implements ChangeListener {
	
	private IDateField datefield;
	
	private ListBox hours;
	
	private ListBox mins;
	
	private ListBox sec;
	
	private ListBox msec;
	
	private ListBox ampm;
	
	private int resolution = IDateField.RESOLUTION_HOUR;
	
	private boolean readonly;
	
	public ITime(IDateField parent) {
		super();
		datefield = parent;
		setStyleName(IDateField.CLASSNAME+"-time");
	}
	
	private void buildTime(boolean redraw) {
		boolean thc = datefield.dts.isTwelveHourClock();
		if(redraw) {
			clear();
			int numHours = thc?12:24;
			hours = new ListBox();
			hours.setStyleName(ISelect.CLASSNAME);
			for(int i=0; i<numHours; i++)
				hours.addItem((i<10)?"0"+i:""+i);
			hours.addChangeListener(this);
			if(thc) {
				ampm = new ListBox();
				ampm.setStyleName(ISelect.CLASSNAME);
				String[] ampmText = datefield.dts.getAmPmStrings();
				ampm.addItem(ampmText[0]);
				ampm.addItem(ampmText[1]);
				ampm.addChangeListener(this);
			}
			
			if(datefield.currentResolution >= IDateField.RESOLUTION_MIN) {
				mins = new ListBox();
				mins.setStyleName(ISelect.CLASSNAME);
				for(int i=0; i<60; i++)
					mins.addItem((i<10)?"0"+i:""+i);
				mins.addChangeListener(this);
			}
			if(datefield.currentResolution >= IDateField.RESOLUTION_SEC) {
				sec = new ListBox();
				sec.setStyleName(ISelect.CLASSNAME);
				for(int i=0; i<60; i++)
					sec.addItem((i<10)?"0"+i:""+i);
				sec.addChangeListener(this);
			}
			if(datefield.currentResolution == IDateField.RESOLUTION_MSEC) {
				msec = new ListBox();
				msec.setStyleName(ISelect.CLASSNAME);
				for(int i=0; i<1000; i++) {
					if(i<10)
						msec.addItem("00"+i);
					else if(i<100)
						msec.addItem("0"+i);
					else msec.addItem(""+i);
				}
				msec.addChangeListener(this);
			}
			
			String delimiter = datefield.dts.getClockDelimeter();
			boolean ro = datefield.readonly;
			
			if(ro) {
				int h = 0;
				if(datefield.date != null)
					h = datefield.date.getHours();
				if(thc) h -= h<12? 0 : 12;
				add(new ILabel(h<10? "0"+h : ""+h));
			} else add(hours);
			
			if(datefield.currentResolution >= IDateField.RESOLUTION_MIN) {
				add(new ILabel(delimiter));
				if(ro) {
					int m = mins.getSelectedIndex();
					add(new ILabel(m<10? "0"+m : ""+m));
				}
				else add(mins);
			}
			if(datefield.currentResolution >= IDateField.RESOLUTION_SEC) {
				add(new ILabel(delimiter));
				if(ro) {
					int s = sec.getSelectedIndex();
					add(new ILabel(s<10? "0"+s : ""+s));
				}
				else add(sec);
			}
			if(datefield.currentResolution == IDateField.RESOLUTION_MSEC) {
				add(new ILabel("."));
				if(ro) {
					int m = datefield.getMilliseconds();
					String ms = m<100? "0"+m : ""+m;
					add(new ILabel(m<10? "0"+ms : ms));
				}
				else add(msec);
			}
			if(datefield.currentResolution == IDateField.RESOLUTION_HOUR) {
				add(new ILabel(delimiter+"00")); // o'clock
			}
			if(thc) {
				add(new ILabel("&nbsp;"));
				if(ro) add(new ILabel(ampm.getItemText(datefield.date.getHours()<12? 0 : 1)));
				else add(ampm);
			}
			
			if(ro) return;
		}
		
		// Update times
		if(thc) {
			int h = datefield.date.getHours();
			ampm.setSelectedIndex(h<12? 0 : 1);
			h -= ampm.getSelectedIndex()*12;
			hours.setSelectedIndex(h);
		} else
			hours.setSelectedIndex(datefield.date.getHours());
		if(datefield.currentResolution >= IDateField.RESOLUTION_MIN)
			mins.setSelectedIndex(datefield.date.getMinutes());
		if(datefield.currentResolution >= IDateField.RESOLUTION_SEC)
			sec.setSelectedIndex(datefield.date.getSeconds());
		if(datefield.currentResolution == IDateField.RESOLUTION_MSEC)
			msec.setSelectedIndex(datefield.getMilliseconds());
		if(thc)
			ampm.setSelectedIndex(datefield.date.getHours()<12?0:1);
		
		if(datefield.readonly && !redraw) {
			// Do complete redraw when in read-only status
			clear();
			String delimiter = datefield.dts.getClockDelimeter();
			
			int h = datefield.date.getHours();
			if(thc) h -= h<12? 0 : 12;
			add(new ILabel(h<10? "0"+h : ""+h));
			
			if(datefield.currentResolution >= IDateField.RESOLUTION_MIN) {
				add(new ILabel(delimiter));
				int m = mins.getSelectedIndex();
				add(new ILabel(m<10? "0"+m : ""+m));
			}
			if(datefield.currentResolution >= IDateField.RESOLUTION_SEC) {
				add(new ILabel(delimiter));
				int s = sec.getSelectedIndex();
				add(new ILabel(s<10? "0"+s : ""+s));
			}
			if(datefield.currentResolution == IDateField.RESOLUTION_MSEC) {
				add(new ILabel("."));
				int m = datefield.getMilliseconds();
				String ms = m<100? "0"+m : ""+m;
				add(new ILabel(m<10? "0"+ms : ms));
			}
			if(datefield.currentResolution == IDateField.RESOLUTION_HOUR) {
				add(new ILabel(delimiter+"00")); // o'clock
			}
			if(thc) {
				add(new ILabel("&nbsp;"));
				add(new ILabel(ampm.getItemText(datefield.date.getHours()<12? 0 : 1)));
			}
		}
		
		boolean enabled = datefield.enabled;
		hours.setEnabled(enabled);
		if(mins != null) mins.setEnabled(enabled);
		if(sec != null) sec.setEnabled(enabled);
		if(msec != null) msec.setEnabled(enabled);
		if(ampm != null) ampm.setEnabled(enabled);
		
	}

	public void updateTime(boolean redraw) {
		buildTime(redraw || resolution != datefield.currentResolution 
						|| readonly != datefield.readonly);
		if(datefield instanceof ITextualDate)
			((ITextualDate) datefield).buildDate();
		resolution = datefield.currentResolution;
		readonly = datefield.readonly;
	}

	public void onChange(Widget sender) {
		if(sender == hours) {
			int h = hours.getSelectedIndex();
			if(datefield.dts.isTwelveHourClock())
				h = h + ampm.getSelectedIndex()*12;
			datefield.date.setHours(h);
			datefield.client.updateVariable(datefield.id, "hour", h, datefield.immediate);
			updateTime(false);
		}
		else if(sender == mins) {
			int m = mins.getSelectedIndex();
			datefield.date.setMinutes(m);
			datefield.client.updateVariable(datefield.id, "min", m, datefield.immediate);
			updateTime(false);
		}
		else if(sender == sec) {
			int s = sec.getSelectedIndex();
			datefield.date.setSeconds(s);
			datefield.client.updateVariable(datefield.id, "sec", s, datefield.immediate);
			updateTime(false);
		}
		else if(sender == msec) {
			int ms = msec.getSelectedIndex();
			datefield.setMilliseconds(ms);
			datefield.client.updateVariable(datefield.id, "msec", ms, datefield.immediate);
			updateTime(false);
		}
		else if(sender == ampm) {
			int h = hours.getSelectedIndex() + ampm.getSelectedIndex()*12;
			datefield.date.setHours(h);
			datefield.client.updateVariable(datefield.id, "hour", h, datefield.immediate);
			updateTime(false);
		}
	}

}
