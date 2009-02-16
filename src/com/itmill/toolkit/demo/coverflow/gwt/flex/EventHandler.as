package {
	import flash.events.EventDispatcher;
	import flash.events.Event;

	/**
	 * This is a simple class created for event handling. Basically it just
	 * dispatches some custom events related to state changes within the coverflow.
	 */
	public class EventHandler extends EventDispatcher {
		// Events
		public static var DATA_CHANGED:String = "data_changed";
		public static var CURRENT_CHANGED:String = "current_changed";
		
		
	}	
}