The Bourbon library has been modified to work around the limitations of the Sass Compiler.
The following changes should be taken into account if Bourbon is upgraded to a newer
version:

file _transition-property-name.scss, function transition-property-name: added space around
the operation '+'. This changed one line from
@return unquote('-'+$vendor+'-'+$prop); 
to @return unquote('-' + $vendor + '-' + $prop);