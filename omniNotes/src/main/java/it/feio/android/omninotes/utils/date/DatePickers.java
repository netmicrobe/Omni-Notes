/*
 * Copyright (C) 2018 Federico Iosue (federico.iosue@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
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

package it.feio.android.omninotes.utils.date;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.DatePicker;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import it.feio.android.omninotes.models.listeners.OnDatePickedListener;
import it.feio.android.omninotes.utils.Constants;
import it.feio.android.omninotes.utils.WiHelper;


public class DatePickers implements OnDateSetListener, OnTimeSetListener {

    public static final int TYPE_GOOGLE = 0;
    public static final int TYPE_AOSP = 1;

    private FragmentActivity mActivity;
    private OnDatePickedListener mOnDatePickedListener;
    private int pickerType;

    private int year;
    private int month;
    private int day;
    private int hourOfDay;
    private int minutes;

    private boolean timePickerCalledAlready = false;
    private long presetDateTime;


    public DatePickers(FragmentActivity mActivity,
                       OnDatePickedListener mOnDatePickedListener, int pickerType) {
        this.mActivity = mActivity;
        this.mOnDatePickedListener = mOnDatePickedListener;
        this.pickerType = pickerType;
    }


    public void pick() {
        pick(null);
    }


    public void pick(Long presetDateTime) {
        this.presetDateTime = DateUtils.getCalendar(presetDateTime).getTimeInMillis();
        if (pickerType == TYPE_AOSP) {
            timePickerCalledAlready = false;
            // Timepicker will be automatically called after date is inserted by user
            showDatePickerDialog(this.presetDateTime);
        } else {
            showDateTimeSelectors(this.presetDateTime);
        }
    }


    /**
     * Show date and time pickers
     */
    protected void showDateTimeSelectors(long reminder) {

        // Sets actual time or previously saved in note
        final Calendar now = DateUtils.getCalendar(reminder);
        DatePickerDialog mCalendarDatePickerDialog = DatePickerDialog.newInstance(
                (DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) -> {
					this.year = year;
					month = monthOfYear;
					day = dayOfMonth;
					TimePickerDialog mRadialTimePickerDialog = TimePickerDialog.newInstance(
                            (radialPickerLayout, hour, minute) -> {
								hourOfDay = hour;
								minutes = minute;

                Calendar c = Calendar.getInstance();
                c.set(year, month, day, hourOfDay, minutes, 0);
                if (mOnDatePickedListener != null) {
                    mOnDatePickedListener.onReminderPicked(c.getTimeInMillis());
                }
							}, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),
							DateUtils.is24HourMode(mActivity));
					mRadialTimePickerDialog.show(mActivity.getSupportFragmentManager(), Constants.TAG);
				}, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        mCalendarDatePickerDialog.show(mActivity.getSupportFragmentManager(), Constants.TAG);
    }


    /**
     * Shows fallback date and time pickers for smaller screens
     */

    public void showDatePickerDialog(long presetDateTime) {
        Bundle b = new Bundle();
        b.putLong(DatePickerDialogFragment.DEFAULT_DATE, presetDateTime);
        DialogFragment picker = new DatePickerDialogFragment();
        picker.setArguments(b);
        WiHelper.logd("== Show Date Picker Dialog!");
        picker.show(mActivity.getSupportFragmentManager(), Constants.TAG);
    }


    private void showTimePickerDialog(long presetDateTime) {
        TimePickerFragment newFragment = new TimePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(TimePickerFragment.DEFAULT_TIME, presetDateTime);
        newFragment.setArguments(bundle);
        WiHelper.logd("== Show Time Picker Dialog!");
        newFragment.show(mActivity.getSupportFragmentManager(), Constants.TAG);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        month = monthOfYear;
        day = dayOfMonth;
        WiHelper.logd("Creation Date: " + year + "-" + month + "-" + day);
        if (!timePickerCalledAlready) {    // Used to avoid native bug that calls onPositiveButtonPressed in the onClose()
            timePickerCalledAlready = true;
            WiHelper.logd("-- Show Time Picker Dialog!");
            showTimePickerDialog(presetDateTime);
        }
    }


    @Override
    public void onTimeSet(android.widget.TimePicker view, int hour, int minute) {
        this.hourOfDay = hour;
        this.minutes = minute;
        WiHelper.logd("Creation Time: " + hour + " : " + minute);
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, hourOfDay, minutes, 0);
        if (mOnDatePickedListener != null) {
            WiHelper.logd("mOnDatePickedListener invoked !");
            mOnDatePickedListener.onReminderPicked(c.getTimeInMillis());
        }
    }

}
