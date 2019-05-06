
class State(object):
    """State abstract base class"""

    def __init__(self):
        pass


    @property
    def name(self):
        """Return the name of teh state"""
        return ''


    def tick(self, now):
        """Handle a tick: one pass through the main loop"""
        pass


    #pylint:disable=unused-argument
    def touch(self, t, touched):
        """Handle a touch event.
        :param (x, y, z) - t: the touch location/strength"""
        return bool(t)


    def enter(self):
        """Just after the state is entered."""
        pass


    def exit(self):
        """Just before the state exits."""
        clear_splash()
