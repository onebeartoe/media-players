
from states import State
#from states.State import State

class Setting_State(State):
    """This state lets the user enable/disable the alarm and set its time.
    Swiping up/down adjusts the hours & miniutes separately."""

    def __init__(self):
        super().__init__()
        self.previous_touch = None
        self.background = 'settings_background.bmp'
        text_area_configs = [dict(x=88, y=120, size=5, color=0xFFFFFF, font=time_font)]

        self.text_areas = create_text_areas(text_area_configs)
        self.buttons = [dict(left=0, top=30, right=80, bottom=93),    # on
                        dict(left=0, top=98, right=80, bottom=152),   # return
                        dict(left=0, top=155, right=80, bottom=220),  # off
                        dict(left=100, top=0, right=200, bottom = 240), # hours
                        dict(left=220, top=0, right=320, bottom = 240)]   # minutes


    @property
    def name(self):
        return 'settings'


    def touch(self, t, touched):
        global alarm_hour, alarm_minute, alarm_enabled
        if t:
            if touch_in_button(t, self.buttons[0]):   # on
                logger.debug('ON touched')
                alarm_enabled = True
                self.text_areas[0].text = '%02d:%02d' % (alarm_hour, alarm_minute)
            elif touch_in_button(t, self.buttons[1]):   # return
                logger.debug('RETURN touched')
                change_to_state('time')
            elif touch_in_button(t, self.buttons[2]): # off
                logger.debug('OFF touched')
                alarm_enabled = False
                self.text_areas[0].text = '     '
            elif alarm_enabled:
                if not self.previous_touch:
                    self.previous_touch = t
                else:
                    if touch_in_button(t, self.buttons[3]):   # HOURS
                        logger.debug('HOURS touched')
                        if t[1] < (self.previous_touch[1] - 5):   # moving up
                            alarm_hour = (alarm_hour + 1) % 24
                            logger.debug('Alarm hour now: %d', alarm_hour)
                        elif t[1] > (self.previous_touch[1] + 5): # moving down
                            alarm_hour = (alarm_hour - 1) % 24
                            logger.debug('Alarm hour now: %d', alarm_hour)
                        self.text_areas[0].text = '%02d:%02d' % (alarm_hour, alarm_minute)
                    elif touch_in_button(t, self.buttons[4]): # MINUTES
                        logger.debug('MINUTES touched')
                        if t[1] < (self.previous_touch[1] - 5):   # moving up
                            alarm_minute = (alarm_minute + 1) % 60
                            logger.debug('Alarm minute now: %d', alarm_minute)
                        elif t[1] > (self.previous_touch[1] + 5): # moving down
                            alarm_minute = (alarm_minute - 1) % 60
                            logger.debug('Alarm minute now: %d', alarm_minute)
                        self.text_areas[0].text = '%02d:%02d' % (alarm_hour, alarm_minute)
                    self.previous_touch = t
            board.DISPLAY.refresh_soon()
            board.DISPLAY.wait_for_frame()
        else:
            self.previous_touch = None
        return bool(t)


    def enter(self):
        global snooze_time
        snooze_time = None

        pyportal.set_background(self.background)
        for ta in self.text_areas:
            pyportal.splash.append(ta)
        if alarm_enabled:
            self.text_areas[0].text = '%02d:%02d' % (alarm_hour, alarm_minute) # set time textarea
        else:
            self.text_areas[0].text = '     '
