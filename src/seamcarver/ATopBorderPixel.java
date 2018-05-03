package seamcarver;

// abstract class for top border pixels, always has a left and can be iterated through
// subclasses are top border pixel and top right border pixel
abstract class ATopBorderPixel extends ABorderPixel implements Iterable<TopBorderPixel> {

}
