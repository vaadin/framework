package sandy.util
{
	import flash.geom.Point;
	
	public class SandyPoint extends Point
	{		
		public var sx : Number;
		public var sy : Number;
		
		public function SandyPoint( x : Number, y : Number, sx : Number, sy : Number )
		{
			super( x, y );
			this.sx = sx;
			this.sy = sy;		
		}
	}
}