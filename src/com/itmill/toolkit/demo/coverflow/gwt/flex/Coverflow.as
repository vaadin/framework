package {
	import flash.events.Event;
    import flash.events.MouseEvent;
    import flash.utils.Dictionary;
    import flash.geom.Matrix;
    
    import mx.controls.Alert;    
    import mx.controls.Image;
    import mx.controls.HScrollBar;
    import mx.core.UIComponent;
    import mx.core.Application;
    import mx.effects.AnimateProperty;
    import mx.effects.easing.Quadratic;
    import flash.external.ExternalInterface;
    import mx.events.ScrollEvent; 

	public class Coverflow extends UIComponent {
		// The scrollbar
		private var _scrollbar:HScrollBar;
		
		// _coverList is an array containing all the coverflow objects		
		private var _coverList:Array = new Array();
		
		// Which element is selected
		private var _selected:int = -1;
		
		// Which element is currently being showed
		private var _current:Number = 0;
		
		// How much space (in pixels) is there between each cover
		private var _coverSpacing:int = 40;
		
		// A map between covers and their positions in the coverflow
		private var _coverMap:Dictionary;
		
		// The angle in which the child covers are distorted
		private static const _angle:int = 35;
		
		// The size of the child covers. The default value is 0.8 which means that
		// the child covers are 80% of their maximum size
		private static const _childScale:Number = 0.8;
		
		// All covers are scaled to the maximum. These two variables tell us how
		// big a cover can be in maximum
		private var _maxWidth:int;
		private var _maxHeight:int;
		
		// An object which takes care of animations
		private var _animation:AnimateProperty;		
		
		// An object for event handling
		private var _eventHandler:EventHandler = new EventHandler();
		
		// The unique identifier of this instance of the widget
		private var _pid:String;
		
		// Has the cover list's content changed?
		private var _listChanged:Boolean = false;
				
		/**
		 * Constructor
		 */
		public function Coverflow():void {			
			super();
			
			// Initialize the scrollbar
			_scrollbar = new HScrollBar();
			_scrollbar.x = 0;			
			// Add an action listener to the scrollbar which detects when the
			// scrollbar's position has been changed. This event should also
			// change the selected cover's value
			_scrollbar.addEventListener(ScrollEvent.SCROLL, function ():void { selectedCover = Math.round(_scrollbar.scrollPosition); });
								
			// Maximize the size of the component
			this.percentHeight = 100;
			this.percentWidth = 100;		

		}
		
		/**
		 * This function is called when the flash has finished loading. This
		 * function will intialize the communication interface between flash
		 * and GWT. 
		 */
		private function init():void {
			// Are we even able to initialize a external communication interface?
			if (ExternalInterface.available) {
				// These two methods are made available for javascript (they
				// can be directly called within javascript code)
	    		ExternalInterface.addCallback("addCover", this.addCover);
	    		ExternalInterface.addCallback("selectCover", this.externalSetCover);
	    		ExternalInterface.addCallback("setBackgroundColor", this.setBackgroundColor);
	    		ExternalInterface.addCallback("toggleScrollbarVisibility", this.toggleScrollbarVisibility);
	    		ExternalInterface.addCallback("removeCover", this.removeCover);		   	    		
	    		
	    		// Try to call a javascript function
                try {
                	// The function we want to call is getCovers. It tells javascript
                	// that the flash is now ready to accept information of the covers.
                	// The name of the function we're about to call is dynamic, meaning
                	// it is unique for every instance of this widget
                   	ExternalInterface.call("itmill.coverflow['getCovers_" + _pid + "']");
                } catch (error:SecurityError) {
                    Alert.show("A SecurityError occurred: " + error.message + "\n");
                } catch (error:Error) {
                    Alert.show("An Error occurred: " + error.message + "\n");
                }
            } else {
                Alert.show("External interface is not available for this container.");
            }	
		}
		
		/**
		 * Adds a new cover to the coverflow list
		 * @param String name
		 * @param String uri
		 */
		private function addCover(name:String, uri:String):void {			
			// Create an instance of the cover object			
			var cover:Cover = new Cover(name.toString(), uri.toString());
			
			// Mark the list as being modified
			_listChanged = true;
			
			// Load the image
			cover.loadImage();
			// Add the cover to our array
			_coverList.push(cover);
			commitProperties();
		}
		
		/**
		 * Measure the size of this component
		 */
		override protected function measure():void {
			super.measure();
			
			// What is the maximum size of a cover? It is either 3/4 of the height of the component
			// or 1/3 of the width - which ever is smaller.
			_maxHeight = _maxWidth = Math.round(Math.min(this.width/3,this.height/4*3));	
		}
	
		/**
		 * Creates the child elements (covers)
		 */
		override protected function createChildren():void {			
			super.createChildren();
			
			for(var i:int = 0; i < _coverList.lenght; i++) 
				addChild(_coverList[i]);			
			
			invalidateDisplayList();				
		}
		
		/**
		 * Something has changed
		 */
		override protected function commitProperties():void  {
			// Remove all old covers			          
            for(i = numChildren-1;i>=0;i--) {
                removeChildAt(numChildren-1);
            }
            
            // Create a new mapping between the covers and their position
            _coverMap = new Dictionary(true);
            
            // Loop through the coverlist array
            for(var i:int = 0;i<_coverList.length;i++) {  
            	var cover:Cover = _coverList[i];
            	// Make sure the cover is loaded
            	cover.loadImage();
            	
            	// Add an event listener to the cover. We want to know when
            	// a cover is being clicked.
            	cover.addEventListener(MouseEvent.CLICK,selectEvent,false,0,true);
            	// Add the cover to the flash movie            	              
                addChildAt(cover,i);     
                
                // Add this cover to the map
                _coverMap[cover] = i;                                             
            }
            
            // Add the scrollbar on top of all other elements
            addChild(_scrollbar);
            
            // Update the scrollbar's details
    		invalidateScrollbar();
            
            // update screen
            invalidateDisplayList();            
         }
    
    	/**
    	 * This function draws the actual content of the flash movie
    	 */
        override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void  {
        	if(_selected < 0) {
        		selectedCover = 0;
        		return;	
        	}
        	
        	// Measure the size of the component
        	measure();
        	// Update the scrollbar's details
        	invalidateScrollbar();
        	
        	// Check if there are any new covers added to our array
        	if(_listChanged) {
        		_eventHandler.dispatchEvent(new Event(EventHandler.DATA_CHANGED));
				_listChanged = false;
        	}
        	
        	// If no covers exist, then do nothing
           	if(_coverList.length == 0)
                return;
            
            
            var cover:Cover;            
            var index:int = 0;
            var m:Matrix;
            
            // Loop through all covers            
            for(var i:int=0; i < _coverList.length; i++) {
	            cover = _coverList[i];
	            
	            // Calculate in which layer position the cover should be 	            	            
	            index = (i <= Math.floor(_current))? i : _coverList.length-1-i;
	                   	       	      	
            	// Set the real size of the cover
            	cover.setActualSize(cover.getExplicitOrMeasuredWidth(),cover.getExplicitOrMeasuredHeight());
            	
            	// Calculate in which angle the cover should be at this specific moment
            	calculateAngle(cover, i);    
            	
            	// Set the covers layer position
            	setChildIndex(cover,index);     
            	
            	// Calculate the cover's size (scale)       	
            	calculateScale(cover, i);
            	
            	// ...and finally calculate its position (horizontal position, that is)
            	calculatePosition(cover, i); 
            	
            	// Resize the cover according to its scale value
            	m = cover.transform.matrix;
            	m.a = m.d = cover.scale;            	
            	cover.transform.matrix = m;       	  
            	
            	// Move the cover to its correct position
            	cover.move(cover.xPos, cover.yPos);
            	
            }                       
           	// Set selected cover on top of all other covers
            setChildIndex(_coverList[Math.floor(_current)],_coverList.length-1);           
        }
        
        /**
         * This function calulcates the position of a cover at any given moment.
         * @param Cover c - Which cover is being processed
         * @param int index - what is the cover's index (horizontal order number)
         */
        private function calculatePosition(c:Cover, index:int):void {
        	// All covers are aligned so, that their bottom is at 3/4's height of the
        	// actual flash movie			
			c.yPos = Math.round(unscaledHeight/4*3-c.getExplicitOrMeasuredHeight()*c.scale);
			
			// Calculate the position difference between the currently selected item and
			// this items index
			var diff:Number = _current-index;
			
			// We want to know the previous and next positions of the item
			var prevPos:Number;
			var nextPos:Number;
						
			// We are currently processing a cover which comes from the left to the center (selected)
			if(index == Math.floor(_current)) {				
				// Calculate the previous position
				prevPos = unscaledWidth/2 - _maxWidth/2 - _coverSpacing;
				// The next position is in the center of the screen
				nextPos = unscaledWidth/2;				
				// Now calculate in which state of the animation we are
				c.xPos = nextPos-diff*(nextPos-prevPos);					
			}	
			// Same as above, except now we come from right to center
			else if(index == Math.ceil(_current)) {
				prevPos = unscaledWidth/2;				
				nextPos = unscaledWidth/2 +_maxWidth/2 + _coverSpacing;
				c.xPos = prevPos-diff*(nextPos-prevPos);						
			}	
			// Child covers to the left
			else if(index < Math.floor(_current)){
				c.xPos = unscaledWidth/2 - _maxWidth/2 - diff*_coverSpacing;				
			}			
			// ..and child covers to the right
			else {
				c.xPos = unscaledWidth/2 +_maxWidth/2 - diff*_coverSpacing;
			}						
		}	
		
		/**
		 * Calulcates the angle of a cover at any given moment
		 * @param Cover c
		 * @param int index
		 */
		private function calculateAngle(c:Cover, index:int):void {
			// This function has the same principle as the the function above
			
			var diff:Number;
						
			if(index == Math.floor(_current)) {				
            	diff = _current-Math.floor(_current);            	
            	c.angle = _angle*diff;            	
            }            
            else if(index == Math.ceil(_current)) {
            	diff = _current-Math.ceil(_current);            	
            	c.angle = _angle*diff;
            }          
            else if(index < Math.floor(_current)) 
            	c.angle = _angle;
            else
            	c.angle = -_angle;
		}
		
		/**
		 * Calculates the size (scale) of a cover at any given moment
		 * @param Cover c
		 * @param int index
		 */
		private function calculateScale(c:Cover, index:int):void {
			// Almost same as calculatePosition, except that now we are
			// calculating the scaled size of the cover
			
			var diff:Number = _current-index;
			var scalePrev:Number;
			var scaleNext:Number;	
			
			if(index == Math.floor(_current)) {
				scalePrev = maxScale(c.width, c.height)*_childScale;
				scaleNext = maxScale(c.width, c.height);
				c.scale = scaleNext-diff*(scaleNext-scalePrev);				
			}
			else if(index == Math.ceil(_current)) {
				scalePrev = maxScale(c.width, c.height);			
				scaleNext = maxScale(c.width, c.height)*_childScale;
				c.scale = scalePrev-diff*(scaleNext-scalePrev);						
			}								
			else
				c.scale = maxScale(c.width, c.height)*_childScale;
			
				
		}
		
		/**
		 * Calculate the maximum scale of a cover
		 * @param Number w - The actual width of the cover
		 * @param Number h - The actual height of the cover
		 * @return Number
		 */
		private function maxScale(w:Number, h:Number):Number {		
			// The width of the cover is bigger than the height. This means that
			// the width will be the restricting factor of this cover's size.
			// Therefore we calculate the maximum size of this cover (result given
			// as a scale)	
			if(w > h)
				return _maxWidth/w;
				
			// Height is bigger than width. Same logic as above.
			else
				return _maxHeight/h;
		}
		
		/**
		 * What happens when a user clicks on a cover?
		 * @param MouseEvent e
		 */
		private function selectEvent(e:MouseEvent):void {
			// Use the mapping between the covers and their positions to
			// find out which is the index of the cover that was clicked.
			// Set this index as the new selected cover.
			selectedCover = _coverMap[e.currentTarget];					
		}
				
		/**
		 * Set the selected cover
		 * @param int index
		 */
		public function set selectedCover(index:int):void {
			// The selected cover is already selected. Do nothing
			if(index == _selected)
				return;
			
			// Validate the index, make sure it's within the coverList's range
			if(index >= 0 && index < _coverList.length) {	
				_selected = index;	
				
				// Animate the changes	
				animateChange();
			}
		}
		
		/**
		 * Returns the selected cover
		 * @return int
		 */
		public function get selectedCover():int {
			return _selected;	
		}
		
		/**
		 * Set the current cover (which cover is actually being shown at this very moment.
		 * Note that the value can be a decimal value, because the selected cover can be for
		 * example 3.2, which means that it has moved 20% between the positions 3 and 4.
		 * @param Number i
		 */
		public function set current(i:Number):void {
			// If ExternalInterface is available and the current cover
			// is same as the currently selected cover (in other words, 
			// the animation has finished), then send the selected cover's
			// key to GWT which will then forward it to the server.		
			if (ExternalInterface.available && i == _selected) {
				ExternalInterface.call("itmill.coverflow['setCurrent_" + _pid + "']",_coverList[_selected].caption);
				
				// Send an event which notifies the scrollbar that the selected cover has changed.
				_eventHandler.dispatchEvent(new Event(EventHandler.CURRENT_CHANGED));				
			}				
			
			// Update value
			_current = i;	
			// Update display	
			invalidateDisplayList();
		}
		
		/**
		 * Get the value of current
		 * @return Number
		 */
		public function get current():Number {
			return _current;	
		}
		
		/**
		 * This is a function where an external source can set the selected cover
		 * (in our case, it could be another widget which is connected to our
		 * coverflow). 
		 * 
		 * @param String index
		 */
		private function externalSetCover(id:String):void {
			var cover:Cover;
			for(var i:int=0; i < _coverList.length; i++) {
	            cover = _coverList[i];
	            
	            if(cover.caption == id) {	            	
	            	selectedCover = i;
	            	break;	
	            }
	        }				
		}
		
		/**
		 * With this function we can remove any cover from the cover flow in run time
		 * 
		 * @param String id
		 */
		private function removeCover(id:String):void {
			var cover:Cover;
			
			// Loop through all covers and search the correct one
			for(var i:int=0; i < _coverList.length; i++) {
	            cover = _coverList[i];
	            
	            // If the cover's name matches with the given id, then delete it
	            if(cover.caption == id) {
	            	// First we will however check if we are removing the last
	            	// which is also currently selected
	            	if(i == _coverList.length-1 && i == _selected) {
	            		// Select the previous cover
	            		selectedCover = i-1;
	            		
	            		// Jump to the end of the animation
	            		if(_animation != null && _animation.isPlaying)
							_animation.end();
	            	}
	            		
	            	// Mark the list as having changes
	            	_listChanged = true;	          
	            	
	            	// Remove the cover from the list  	          	
	            	_coverList.splice(i,1);
	            	
	            	// Update
	            	commitProperties();
	            	break;	
	            }
	        }
		}
		
		/**
		 * Gives a references to the event handler
		 * @return EventHandler
		 */
		public function get eventHandler():EventHandler {
			return _eventHandler;	
		}
		
		/**
		 * Tells us how many covers there are currently added
		 * @return int
		 */
		public function get coverCount():int {
			return _coverList.length;	
		}	
		
		/**
		 * Sets the unique identifier of this widget. This is used to create
		 * the dynamic function names in the external interface calls.
		 * @param String p
		 */
		public function set pid(p:String):void {
			if(_pid == null) {
				_pid = p;
				init();	
			}	
		}	
		
		/**
		 * Set the background color of the coverflow
		 * @param String gradientStart
		 * @param String gradientEnd
		 */
		public function setBackgroundColor(gradientStart:String, gradientEnd:String):void {			
			Application.application.setStyle('backgroundGradientColors', [gradientStart, gradientEnd]);	
		}	
		
		/**
		 * Make sure the scrollbar is up-to-date, both in size and position wise
		 */
		private function invalidateScrollbar():void {			
			_scrollbar.width = unscaledWidth;
			_scrollbar.y = unscaledHeight-_scrollbar.getExplicitOrMeasuredHeight()/2;
			_scrollbar.maxScrollPosition = coverCount-1;
			_scrollbar.scrollPosition = _selected;
			_scrollbar.pageSize = 1;
			_scrollbar.invalidateDisplayList();
		}
		
		/**
		 * Change the visibility status of the scrollbar
		 * @param String visibility
		 */
		public function toggleScrollbarVisibility(visibility:String):void {
			// Input is a string, because javascript can call directly on this function
			if(visibility == "false")
				_scrollbar.visible = false;
			else
				_scrollbar.visible = true;
		}
		
		/**
		 * Animate the state changes
		 */
		private function animateChange():void {
			// If there already is an animation in process, stop it (jump to the end) and start the new one.
			if(_animation != null && _animation.isPlaying)
				_animation.end();
				
			// Set up a new animation. We want the "current" value go to the "_selected" value
			_animation = new AnimateProperty(this);
            _animation.property = "current";
            _animation.toValue = _selected;
            _animation.target = this;
			
			// What is the duration of the animation? We don't want the animation to go too fast or too slow.
			// An animation where the selected cover is changed with only one position should be relatively
			// slow, so that we actually can se an animation. Therefore we've set a minimum length of the 
			// animation to 400 ms. If we jump over several covers, then we want the animation to be a
			// bit longer, so that the animation would be smoother. Therefore we reserve 200ms between every
			// cover change. Select which ever is bigger, 400ms or difference between current and selected 
			// cover * 200ms.
			var duration:int = Math.max(400,Math.abs(Math.ceil(_current)-_selected)*200);
			
			// If we do long jumps (for example by using the slider), we don't want the animation to go 
			// too long. Therefore we're setting a maximum length for the animation, in this case 2 seconds.
			duration = Math.min(2000,duration);
			_animation.duration = duration;
			
			// We don't want the animation to be linear, we want it to slow down in the end
            _animation.easingFunction = mx.effects.easing.Quadratic.easeOut;
            
            // Start the animation
            _animation.play();	
		}	

	}
	
	
}
	