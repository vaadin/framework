package {
	import flash.events.Event;
	import flash.display.Loader;
	import flash.display.DisplayObject;
	import flash.system.LoaderContext;
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.net.URLRequest;
	import mx.controls.Alert;
	
	import flash.system.Security;
	import flash.system.ApplicationDomain;
    
    import mx.controls.Image;
    import mx.core.UIComponent;
    import flash.geom.Matrix;
    import flash.display.Sprite;
    import flash.display.Shape;
    import flash.display.Graphics;
    import flash.display.GradientType;
    import flash.geom.Rectangle;
    import flash.geom.Point;
    
    import sandy.util.*;

	public class Cover extends UIComponent {
		// Cover information					
		private var _caption:String;
		private var _uri:String;
		private var _imageLoaded:Boolean = false;
		
		// Actual content information
		private var _bitmapData:BitmapData;
		private var _bitmap:Bitmap;
		private var _img:Image;
		private var _distortedShape:Shape;
		private var _reflectionBitmap:Bitmap;
		private var _reflectionShape:Shape;

		
		// Distort information
		private var _realAngle:Number = -1;
		private var _realScale:Number = -1;
		private var _angle:Number = 0;	
		private var _scale:Number = 1;
		private static const perspectiveConstant:Number = .15;
		
				
		// Position information
		private var _x:int;
		private var _y:int;
					
		/**
		 * Constructor
		 */
		public function Cover(caption:String, uri:String) {
			super();		
						
			// Set this element's size to 100% x 100%
			super.percentHeight = 100;
			super.percentWidth = 100;
			
			// Store input data
			this._caption = caption;
			this._uri = uri;
			
			// Initialize default image
			this._img = new Image();			
			this._img.percentHeight = 100;
			this._img.percentWidth = 100;
			this._bitmapData = new BitmapData(100,100,false, 0xffff0000);
						 
			_distortedShape = new Shape();
			addChild(_distortedShape);
			
			_reflectionShape = new Shape();
			addChild(_reflectionShape);
			
			// Create the default image
			this._img.source = this.getBitmap();
			
			addChildAt(_img, 0);			
		}
		
		/**
		 * Load an image if it hasn't been loaded yet
		 */		
		public function loadImage():void {
			if(!_imageLoaded) {
				// Create a loader to load the image
				var request:URLRequest = new URLRequest(this._uri);	
				var imageLoader:Loader = new Loader();		
				
				// Check for restrictions
				var imgLdrContext:LoaderContext = new LoaderContext(false, ApplicationDomain.currentDomain);
				imgLdrContext.checkPolicyFile = true;
				
				// Set an event listener
	 			imageLoader.contentLoaderInfo.addEventListener(Event.COMPLETE, imgLoaded);
	 			
	 			// Load the image
 				imageLoader.load(request, imgLdrContext);
 			}
		}
		
		/**
		 * Event handler for image load completion
		 * @param Event event
		 */
		private function imgLoaded(event:Event):void {
			// Check if we got the image		
			try {
				removeChild(_img);				  
				this._bitmapData = Bitmap(event.currentTarget.content).bitmapData;
								
				// Set new source for the image
				this._img.source = this.getBitmap();
				addChildAt(_img, 0);
				
				_imageLoaded = true;		
				invalidateDisplayList();							
			}
			catch(error:Error) {
				// An error occured
				Alert.show(error.toString());
			}	
		}		
		
		/**
		 * Measure the size of the image
		 */		
		override protected function measure():void {
            if(_img != null)
            {
                measuredHeight = _img.getExplicitOrMeasuredHeight();
                measuredWidth = _img.getExplicitOrMeasuredWidth();
            }
        }
        
        /**
         * Update the image on the display
         */ 
        override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void  {
        	// Do this if and only iff the angle or the scale has changed. Otherwise we do not
        	// want to re-draw the image, only move it.            
            if(_img && (_realAngle != _angle || _realScale != _scale))            
            {
                var contentWidth:Number = _img.getExplicitOrMeasuredWidth();
                var contentHeight:Number= _img.getExplicitOrMeasuredHeight();
       
                _img.setActualSize(contentWidth,contentHeight);
 
 				_realAngle = _angle;
 				_realScale = _scale;
 				
 				// Distort the image
               	distortImage(_bitmap, _distortedShape, false);
               	
               	// Create a reflection
               	createReflectionBitmap(_bitmap);
            }
        }
   	
   		/**
   		 * This function distorts the target according to its angle
   		 * @param Bitmap bm - The bitmap of the object which is being distorted
   		 * @param Shape target - The target where the distorted image is stored
   		 * @param Boolean refelection - Is this target a reflection? 
   		 */
        private function distortImage(bm:Bitmap, target:Shape, reflection:Boolean):void {
        	// Turn the angle into a value between 0 and 1
        	var k:Number = (Math.abs(_angle)/90);
        	k = Math.sqrt(k);        	
        	
        	var vShear:Number = (_angle >= 0)? k*-perspectiveConstant : k*perspectiveConstant;
        	var verticalOffset:Number;
        	
        	// How much is the width of the image scaled
            var hScale:Number = 1 - k;
                           	
            // How much lower/higher are the corners "further away" than the one "closer"
            verticalOffset = (_img.getExplicitOrMeasuredHeight()*vShear);
            
            // Is this image a reflection? If yes, then the vertical offset distortion is made in another direction
            if(reflection)
            	verticalOffset *= -1;
        	
        	// Initialize the distortion class
        	var distort:DistortImage = new DistortImage();
			distort.container = target;
			distort.target = bm;
			distort.smooth = true;
			distort.initialize( 5, 5, null );		
			
			// Distort the images to the left
			if(_angle > 0) {						
				distort.setTransform(
					0,0,
					_img.getExplicitOrMeasuredWidth()*hScale,-verticalOffset,
					_img.getExplicitOrMeasuredWidth()*hScale,_img.getExplicitOrMeasuredHeight()+verticalOffset,
					0,_img.getExplicitOrMeasuredHeight()
					);			
			}
			// Distort the images to the right
			else if(_angle < 0) {						
				distort.setTransform(
					0,verticalOffset,
					_img.getExplicitOrMeasuredWidth()*hScale,0,
					_img.getExplicitOrMeasuredWidth()*hScale,_img.getExplicitOrMeasuredHeight(),
					0,_img.getExplicitOrMeasuredHeight()-verticalOffset
					);			
			}
			distort.render();
			
			// We use the original image as a reference, but do not want to show it on the screen			
			_img.visible = false;
         	
         	// Align the distorted image correctly
         	var m:Matrix = target.transform.matrix;
			m.tx = 0 - _img.width/2* hScale;
			target.transform.matrix = m;  
        }
        
        /**
        * This function creates a reflection of the target object
        * @param DisplayObject target
        */
        private function createReflectionBitmap(target:DisplayObject):void {
        	// Size of the fade
			var fadeSize:Number = 0.4;
			
			// Create a rectangle
			var box:Rectangle = new Rectangle(0, 0, target.width, target.height);
							
			// Create a matrix for the gradient		
			var gradientMatrix: Matrix = new Matrix();
			// Create a shape object for the gradient
			var gradientShape: Shape = new Shape();
			// Apply a gradient on the matrix
			gradientMatrix.createGradientBox(target.width, target.height * fadeSize, Math.PI/2,	0, target.height * (1.0 - fadeSize));
			
			// Fill the shape with the gradient
			gradientShape.graphics.beginGradientFill(GradientType.LINEAR, [0xFFFFFF, 0xFFFFFF],	[0, 1], [0, 255], gradientMatrix);
			gradientShape.graphics.drawRect(0, target.height * (1.0 - fadeSize), target.width, target.height * fadeSize);
			gradientShape.graphics.endFill();
			
			// Bitmap representation of the gradient
			var gradientBm:BitmapData = new BitmapData(target.width, target.height, true, 0x00000000);
			gradientBm.draw(gradientShape, new Matrix());			
			
			var targetBm:BitmapData = new BitmapData(target.width, target.height, true, 0x00000000);
			targetBm.fillRect(box, 0x00000000);
			targetBm.draw(target, new Matrix());
			
			var reflectionData:BitmapData = new BitmapData(target.width, target.height, true, 0x00000000);
			reflectionData.fillRect(box, 0x00000000);
			reflectionData.copyPixels(targetBm, box, new Point(), gradientBm);
			
			// Store the reflection
			_reflectionBitmap = new Bitmap(reflectionData);
			
			// Move the reflection to its correct position
			var m:Matrix = _distortedShape.transform.matrix;
			m.d = -1;
			m.ty = _distortedShape.height*2;
			_reflectionShape.transform.matrix = m;
			
			// Distort the reflection 
			distortImage(_reflectionBitmap, _reflectionShape, true);
			
			// Set a transparancy for the reflection
			_reflectionShape.alpha = 0.4;
			
		}
       
		/**
		 * Get name
		 * @return String
		 */
		public function get caption():String {
			return this._caption;	
		}
		
		/**
		 * Set name
		 * @param String name
		 */
		public function set caption(caption:String):void {
			this._caption = caption;	
		}
		
		/**
		 * Get uri
		 * @return String
		 */
		public function get uri():String {
			return this._uri;	
		}
		
		/**
		 * Set uri
		 * @param String uri
		 */
		public function set uri(uri:String):void {
			this._uri = uri;	
		}
		
		/**
		 * Get the bitmap
		 * @return Bitmap
		 */
		public function getBitmap():Bitmap {
			this._bitmap = new Bitmap(this._bitmapData);
			return this._bitmap;
		}	
		
		/**
		 * Get the bitmap data
		 * @return BitmapData
		 */
		public function getBitmapData():BitmapData {
			return this._bitmapData;
		}
		
		/**
		 * Set the angle of the cover
		 * @param Number angle
		 */
		public function set angle(angle:Number):void {
			this._angle = angle;
			invalidateDisplayList();			
		}	
		
		/**
		 * Get the angle of the cover
		 * @return Number
		 */
		public function get angle():Number {
			return this._angle;
		}
		
		/**
		 * Set the x position of the cover
		 * @param Number x
		 */
		public function set xPos(x:Number):void {
			this._x = x;
		}	
		
		/**
		 * Get the x position of the cover
		 * @return Number
		 */
		public function get xPos():Number {
			return this._x;
		}
		
		/**
		 * Set the y position of the cover
		 * @param Number y
		 */
		public function set yPos(y:Number):void {
			this._y = y;
		}	
		
		/**
		 * Get the y position of the cover
		 * @return Number
		 */
		public function get yPos():Number {
			return this._y;
		}
		
		/**
		 * Set the scale of the cover
		 * @param Number scale
		 */
		public function set scale(scale:Number):void {
			this._scale = scale;	
		}
		
		/**
		 * Get the scale position of the cover
		 * @return Number
		 */
		public function get scale():Number {
			return this._scale;	
		}
		
	}
}
	