package seamcarver;

// abstract class for left border pixels, always has a top and can be iterated through
// subclasses are left border pixel and bottom left border pixel
abstract class ALeftBorderPixel extends ABorderPixel implements Iterable<LeftBorderPixel> {

}
