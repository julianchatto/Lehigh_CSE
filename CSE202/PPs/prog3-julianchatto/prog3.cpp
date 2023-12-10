#include "image.h"
#include <time.h>
using ImagePtr = std::shared_ptr<Image>;

ImagePtr applyGamma(ImagePtr image_ptr, double gamma);
ImagePtr applyGamma_1(ImagePtr image_ptr, double gamma);
ImagePtr applyGamma_2(ImagePtr image_ptr, double gamma);
ImagePtr applyGamma_3(ImagePtr image_ptr, double gamma);
ImagePtr applyTint(ImagePtr image_ptr, const double *tints);
ImagePtr applyBlur(ImagePtr imag_ptr);
ImagePtr applyBlur_1(ImagePtr imag_ptr);
ImagePtr applyBlur_2(ImagePtr imag_ptr);
ImagePtr applyBlur_3(ImagePtr imag_ptr);
void writeImage(ImagePtr image_ptr);

void process_images(const std::vector<ImagePtr>& image_vector) {
	const double tint_array[] = {0.75, 0, 0};
	for (ImagePtr img : image_vector) {
		writeImage(img);
		img = applyGamma(img, 1.4); 
		img = applyTint(img, tint_array);
		img = applyBlur(img);
		writeImage(img);
	}
  
}
// Apply a Gamma scale to all the pixels of the input image
ImagePtr applyGamma(ImagePtr image_ptr, double gamma) {
	auto output_image_ptr = std::make_shared<Image>(image_ptr->name() + "_gamma", IMAGE_WIDTH, IMAGE_HEIGHT);
	auto in_rows = image_ptr->rows();
	auto out_rows = output_image_ptr->rows();
	//const int height = in_rows.size();
	//const int width = in_rows[1] - in_rows[0];
	for (unsigned long i = 0; i < in_rows.size(); ++i ) {
		for (int j = 0; j < in_rows[1] - in_rows[0]; ++j ) {
		//const Pixel& p = in_rows[i][j]; 
			double v = 0.3*in_rows[i][j].bgra[2] + 0.59*in_rows[i][j].bgra[1] + 0.11*in_rows[i][j].bgra[0];
			double res = pow(v, gamma);
			if(res > MAX_BGR_VALUE) {
				res = MAX_BGR_VALUE;
			}
			out_rows[i][j] = Pixel(res, res, res);
		}
		
	}
	return output_image_ptr;
}

// used code motion, reduced memory references, and reduced function calls
ImagePtr applyGamma_1(ImagePtr image_ptr, double gamma) {
	auto output_image_ptr = std::make_shared<Image>(image_ptr->name() + "_gamma", IMAGE_WIDTH, IMAGE_HEIGHT);
	auto in_rows = image_ptr->rows();
	auto out_rows = output_image_ptr->rows();
	const int height = in_rows.size(); // code motion
	const int width = in_rows[1] - in_rows[0]; // code motion
	for (long i = 0; i < height; ++i ) {
		for (int j = 0; j < width; ++j ) {
			const Pixel& p = in_rows[i][j]; 
			double v = 0.3*p.bgra[2] + 0.59*p.bgra[1] + 0.11*p.bgra[0];
			double res = pow(v, gamma);

			if(res > MAX_BGR_VALUE) {
				res = MAX_BGR_VALUE;
			}
			out_rows[i][j] = Pixel(res, res, res);
		}
		
	}
	return output_image_ptr;
}

// 3x3 loop unrolling and code motion
ImagePtr applyGamma_2(ImagePtr image_ptr, double gamma) {
	auto output_image_ptr = std::make_shared<Image>(image_ptr->name() + "_gamma", IMAGE_WIDTH, IMAGE_HEIGHT);
	auto in_rows = image_ptr->rows();
	auto out_rows = output_image_ptr->rows();
	int width = in_rows[1] - in_rows[0];
	int height = in_rows.size();
	for ( long i = 0; i < height; i++ ) {
		int limit = width - 2;
		int j = 0;
		for (; j < limit; j += 3) {
			double v1 = 0.3*in_rows[i][j].bgra[2] + 0.59*in_rows[i][j].bgra[1] + 0.11*in_rows[i][j].bgra[0];
			double v2 = 0.3*in_rows[i][j + 1].bgra[2] + 0.59*in_rows[i][j + 1].bgra[1] + 0.11*in_rows[i][j + 1].bgra[0];
			double v3 = 0.3*in_rows[i][j + 2].bgra[2] + 0.59*in_rows[i][j + 2].bgra[1] + 0.11*in_rows[i][j + 2].bgra[0];
			double res1 = pow(v1, gamma);
			double res2 = pow(v2, gamma);
			double res3 = pow(v3, gamma);
			if(res1 > MAX_BGR_VALUE) {
				res1 = MAX_BGR_VALUE;
			}
			if(res2 > MAX_BGR_VALUE) {
				res2 = MAX_BGR_VALUE;
			}
			if(res3 > MAX_BGR_VALUE) {
				res3 = MAX_BGR_VALUE;
			}

			out_rows[i][j] = Pixel(res1, res1, res1);
			out_rows[i][j + 1] = Pixel(res2, res2, res2);
			out_rows[i][j + 2] = Pixel(res3, res3, res3);
		}
		for (; j < width; j++) {
			double v = 0.3*in_rows[i][j].bgra[2] + 0.59*in_rows[i][j].bgra[1] + 0.11*in_rows[i][j].bgra[0];
			double res = pow(v, gamma);
			if(res > MAX_BGR_VALUE) {
				res = MAX_BGR_VALUE;
			}
			out_rows[i][j] = Pixel(res, res, res);
		}
		
	}
	return output_image_ptr;
}

// 6x6 loop unrolling, code motion, and reduced memory references
ImagePtr applyGamma_3(ImagePtr image_ptr, double gamma) {
	auto output_image_ptr = std::make_shared<Image>(image_ptr->name() + "_gamma", IMAGE_WIDTH, IMAGE_HEIGHT);
	auto in_rows = image_ptr->rows();
	auto out_rows = output_image_ptr->rows();
	const int height = in_rows.size(); // code motion
	const int width = in_rows[1] - in_rows[0]; // code motion
	for (long i = 0; i < height; i++) {
		int limit = width - 5;
		int j;
		for (j = 0; j < limit; j+=6) {
			const Pixel& p1 = in_rows[i][j]; 
			const Pixel& p2 = in_rows[i][j + 1];
			const Pixel& p3 = in_rows[i][j + 2];
			const Pixel& p4 = in_rows[i][j + 3];
			const Pixel& p5 = in_rows[i][j + 4];
			const Pixel& p6 = in_rows[i][j + 5];

			double v1 = 0.3*p1.bgra[2] + 0.59*p1.bgra[1] + 0.11*p1.bgra[0];
			double v2 = 0.3*p2.bgra[2] + 0.59*p2.bgra[1] + 0.11*p2.bgra[0];
			double v3 = 0.3*p3.bgra[2] + 0.59*p3.bgra[1] + 0.11*p3.bgra[0];
			double v4 = 0.3*p4.bgra[2] + 0.59*p4.bgra[1] + 0.11*p4.bgra[0];
			double v5 = 0.3*p5.bgra[2] + 0.59*p5.bgra[1] + 0.11*p5.bgra[0];
			double v6 = 0.3*p6.bgra[2] + 0.59*p6.bgra[1] + 0.11*p6.bgra[0];

			double res1 = pow(v1, gamma);
			double res2 = pow(v2, gamma);
			double res3 = pow(v3, gamma);
			double res4 = pow(v4, gamma);
			double res5 = pow(v5, gamma);
			double res6 = pow(v6, gamma);

			if(res1 > MAX_BGR_VALUE) {
				res1 = MAX_BGR_VALUE;
			}
			if(res2 > MAX_BGR_VALUE) {
				res2 = MAX_BGR_VALUE;
			}
			if(res3 > MAX_BGR_VALUE) {
				res3 = MAX_BGR_VALUE;
			}
			if(res4 > MAX_BGR_VALUE) {
				res4 = MAX_BGR_VALUE;
			}
			if(res5 > MAX_BGR_VALUE) {
				res5 = MAX_BGR_VALUE;
			}
			if(res6 > MAX_BGR_VALUE) {
				res6 = MAX_BGR_VALUE;
			}
			out_rows[i][j] = Pixel(res1, res1, res1);
			out_rows[i][j + 1] = Pixel(res2, res2, res2);
			out_rows[i][j + 2] = Pixel(res3, res3, res3);
			out_rows[i][j + 3] = Pixel(res4, res4, res4);
			out_rows[i][j + 4] = Pixel(res5, res5, res5);
			out_rows[i][j + 5] = Pixel(res6, res6, res6);
		}
		for (; j < width; j++) {
			double v = 0.3*in_rows[i][j].bgra[2] + 0.59*in_rows[i][j].bgra[1] + 0.11*in_rows[i][j].bgra[0];
			double res = pow(v, gamma);
			if(res > MAX_BGR_VALUE) {
				res = MAX_BGR_VALUE;
			}
			out_rows[i][j] = Pixel(res, res, res);
		}
		
	}
	return output_image_ptr;
}

// Apply the tint in the array tints to all the pixels of the input image
ImagePtr applyTint(ImagePtr image_ptr, const double *tints) {
  	auto output_image_ptr = 
    std::make_shared<Image>(image_ptr->name() + "_tinted", IMAGE_WIDTH, IMAGE_HEIGHT);
	auto in_rows = image_ptr->rows();
	auto out_rows = output_image_ptr->rows();

	for (unsigned long i = 0; i < image_ptr->rows().size(); ++i ) {
		for (int j = 0; j < image_ptr->rows()[1] - image_ptr->rows()[0]; ++j ) {
			double b = (double)in_rows[i][j].bgra[0] + (MAX_BGR_VALUE-in_rows[i][j].bgra[0])*tints[0];
			double g = (double)in_rows[i][j].bgra[1] + (MAX_BGR_VALUE-in_rows[i][j].bgra[1])*tints[1];
			double r = (double)in_rows[i][j].bgra[2] + (MAX_BGR_VALUE-in_rows[i][j].bgra[0])*tints[2];
			out_rows[i][j].bgra[0] = b > MAX_BGR_VALUE ? MAX_BGR_VALUE:b;
			out_rows[i][j].bgra[1] = g > MAX_BGR_VALUE ? MAX_BGR_VALUE:g;
			out_rows[i][j].bgra[2] = r > MAX_BGR_VALUE ? MAX_BGR_VALUE:r;
		}
	}
  	return output_image_ptr;
}

ImagePtr applyBlur(ImagePtr image_ptr) {
  	auto output_image_ptr = 
    std::make_shared<Image>(image_ptr->name() + "_blurred", IMAGE_WIDTH, IMAGE_HEIGHT);
	auto in_rows = image_ptr->rows();
	auto out_rows = output_image_ptr->rows();
	double b, g, r;
	for (unsigned long i = 0; i < in_rows.size(); ++i ) {
		for (int j = 0; j < in_rows[1] - in_rows[0]; ++j ) {
			if (i == 0) {                        /* first row */
				if (j == 0) {                     /* first row, first column */
					b = (0 + 0 + 0 + 0 + in_rows[i][j].bgra[0] + in_rows[i+1][j].bgra[0] + 0 + in_rows[i][j+1].bgra[0] + in_rows[i+1][j+1].bgra[0]) / 9;
					g = (0 + 0 + 0 + 0 + in_rows[i][j].bgra[1] + in_rows[i+1][j].bgra[1] + 0 + in_rows[i][j+1].bgra[1] + in_rows[i+1][j+1].bgra[1]) / 9;
					r = (0 + 0 + 0 + 0 + in_rows[i][j].bgra[2] + in_rows[i+1][j].bgra[2] + 0 + in_rows[i][j+1].bgra[2] + in_rows[i+1][j+1].bgra[2]) / 9;
				} 
				else if (j == in_rows[1] - in_rows[0] - 1) {          /* first row, last column */
					b = (0 + 0 + 0 + in_rows[i][j-1].bgra[0] + in_rows[i][j].bgra[0] + 0 + in_rows[i+1][j-1].bgra[0] + in_rows[i+1][j].bgra[0] + 0) / 9;
					g = (0 + 0 + 0 + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + 0 + in_rows[i+1][j-1].bgra[1] + in_rows[i+1][j].bgra[1] + 0) / 9;
					r = (0 + 0 + 0 + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + 0 + in_rows[i+1][j-1].bgra[2] + in_rows[i+1][j].bgra[2] + 0) / 9;
				} 
				else {                          /* first row, middle columns */
					b = (0 + 0 + 0 + in_rows[i][j-1].bgra[0] + in_rows[i][j].bgra[0] + in_rows[i][j+1].bgra[0] + in_rows[i+1][j-1].bgra[0] + in_rows[i+1][j].bgra[0] + in_rows[i+1][j+1].bgra[0]) / 9;
					g = (0 + 0 + 0 + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + in_rows[i][j+1].bgra[1] + in_rows[i+1][j-1].bgra[1] + in_rows[i+1][j].bgra[1] + in_rows[i+1][j+1].bgra[1]) / 9;
					r = (0 + 0 + 0 + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + in_rows[i][j+1].bgra[2] + in_rows[i+1][j-1].bgra[2] + in_rows[i+1][j].bgra[2] + in_rows[i+1][j+1].bgra[2]) / 9;
				}
			} 
			else if (i == in_rows.size() - 1) {        /* last row */
				if (j == 0) {             /* last row, first column */
					b = (0 + in_rows[i-1][j].bgra[0] + in_rows[i-1][j+1].bgra[0] + 0 + in_rows[i][j].bgra[0] + in_rows[i][j+1].bgra[0] + 0 + 0 + 0) / 9;
					g = (0 + in_rows[i-1][j].bgra[1] + in_rows[i-1][j+1].bgra[1] + 0 + in_rows[i][j].bgra[1] + in_rows[i][j+1].bgra[1] + 0 + 0 + 0) / 9;
					r = (0 + in_rows[i-1][j].bgra[2] + in_rows[i-1][j+1].bgra[2] + 0 + in_rows[i][j].bgra[2] + in_rows[i][j+1].bgra[2] + 0 + 0 + 0) / 9;
				} 
				else if (j == in_rows[1] - in_rows[0] - 1) {      /* last row, last column */
					b = (in_rows[i-1][j-1].bgra[0] + in_rows[i-1][j+1].bgra[0] + 0 + in_rows[i][i-1].bgra[0] + in_rows[i][j].bgra[0] + 0 + 0 + 0 + 0) / 9;
					g = (in_rows[i-1][j-1].bgra[1] + in_rows[i-1][j+1].bgra[1] + 0 + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + 0 + 0 + 0 + 0) / 9;
					r = (in_rows[i-1][j-1].bgra[2] + in_rows[i-1][j+1].bgra[2] + 0 + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + 0 + 0 + 0 + 0) / 9;
				} 
				else {                          /* last row, middle columns */
					b = (in_rows[i-1][j-1].bgra[0] + in_rows[i-1][j].bgra[0] + in_rows[i-1][j+1].bgra[0] + in_rows[i][j-1].bgra[0] + in_rows[i][j].bgra[0] + in_rows[i][j+1].bgra[0] + 0 + 0 + 0) / 9;
					g = (in_rows[i-1][j-1].bgra[1] + in_rows[i-1][j].bgra[1] + in_rows[i-1][j+1].bgra[1] + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + in_rows[i][j+1].bgra[1] + 0 + 0 + 0) / 9;
					r = (in_rows[i-1][j-1].bgra[2] + in_rows[i-1][j].bgra[2] + in_rows[i-1][j+1].bgra[2] + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + in_rows[i][j+1].bgra[2] + 0 + 0 + 0) / 9;
				}
			} 
			else {                            /* middle rows */
				if (j == 0) {                 /* middle row, first column */
					b = ( 0 + in_rows[i-1][j].bgra[0] + in_rows[i-1][j+1].bgra[0] + 0 + in_rows[i][j].bgra[0] + in_rows[i][j+1].bgra[0] + 0 + in_rows[i+1][j].bgra[0] + in_rows[i+1][j+1].bgra[0]) / 9;
					g = ( 0 + in_rows[i-1][j].bgra[1] + in_rows[i-1][j+1].bgra[1] + 0 + in_rows[i][j].bgra[1] + in_rows[i][j+1].bgra[1] + 0 + in_rows[i+1][j].bgra[1] + in_rows[i+1][j+1].bgra[1]) / 9;
					r = ( 0 + in_rows[i-1][j].bgra[2] + in_rows[i-1][j+1].bgra[2] + 0 + in_rows[i][j].bgra[2] + in_rows[i][j+1].bgra[2] + 0 + in_rows[i+1][j].bgra[2] + in_rows[i+1][j+1].bgra[2]) / 9;
				} 
				else if (j == in_rows[1] - in_rows[0] - 1) {      /* middle row, last column */
					b = ( in_rows[i-1][j-1].bgra[0] + in_rows[i-1][j].bgra[0] + 0 + in_rows[i][j-1].bgra[0] + in_rows[i][j].bgra[0] + 0 + in_rows[i+1][j-1].bgra[0]+ in_rows[i+1][j].bgra[0] + 0) / 9;
					g = ( in_rows[i-1][j-1].bgra[1] + in_rows[i-1][j].bgra[1] + 0 + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + 0 + in_rows[i+1][j-1].bgra[1] + in_rows[i+1][j].bgra[1] + 0) / 9;
					r = ( in_rows[i-1][j-1].bgra[2] + in_rows[i-1][j].bgra[2] + 0 + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + 0 + in_rows[i+1][j-1].bgra[2] + in_rows[i+1][j].bgra[2] + 0) / 9;
				} 
				else {                          /* middle row, middle columns */
					b = ( in_rows[i-1][j-1].bgra[0] + in_rows[i-1][j].bgra[0] + in_rows[i-1][j+1].bgra[0] + in_rows[i][j-1].bgra[0] + in_rows[i][j].bgra[0] + in_rows[i][j+1].bgra[0] + in_rows[i+1][j-1].bgra[0] + in_rows[i+1][j].bgra[0] + in_rows[i+1][j+1].bgra[0]) / 9;
					g = ( in_rows[i-1][j-1].bgra[1] + in_rows[i-1][j].bgra[1] + in_rows[i-1][j+1].bgra[1] + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + in_rows[i][j+1].bgra[1] + in_rows[i+1][j-1].bgra[1] + in_rows[i+1][j].bgra[1] + in_rows[i+1][j+1].bgra[1]) / 9;
					r = ( in_rows[i-1][j-1].bgra[2] + in_rows[i-1][j].bgra[2] + in_rows[i-1][j+1].bgra[2] + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + in_rows[i][j+1].bgra[2] + in_rows[i+1][j-1].bgra[2] + in_rows[i+1][j].bgra[2] + in_rows[i+1][j+1].bgra[2]) / 9;
				}
			}
			out_rows[i][j].bgra[0] = (b > MAX_BGR_VALUE) ? MAX_BGR_VALUE : b;
			out_rows[i][j].bgra[1] = (g > MAX_BGR_VALUE) ? MAX_BGR_VALUE : g;
			out_rows[i][j].bgra[2] = (r > MAX_BGR_VALUE) ? MAX_BGR_VALUE : r;
			}
	}
	return output_image_ptr;
}

// used code motion, reduced memory references, and reduced function calls
ImagePtr applyBlur_1(ImagePtr image_ptr) {
  	auto output_image_ptr = 
    std::make_shared<Image>(image_ptr->name() + "_blurred", IMAGE_WIDTH, IMAGE_HEIGHT);
	auto in_rows = image_ptr->rows();
	auto out_rows = output_image_ptr->rows();
	double b, g, r;
	unsigned height = in_rows.size();
	const int width = in_rows[1] - in_rows[0];
	for (unsigned i = 0; i < height; ++i ) {
		for (int j = 0; j < width; ++j ) {
			const Pixel& pij = in_rows[i][j];
			const Pixel& pi1j = in_rows[i+1][j];
			const Pixel& pij1 = in_rows[i][j+1];
			const Pixel& pi1j1 = in_rows[i+1][j+1];

			if (i == 0) {                        /* first row */
				if (j == 0) {                     /* first row, first column */
					b = (0 + 0 + 0 + 0 + pij.bgra[0] + pi1j.bgra[0] + 0 + pij1.bgra[0] + pi1j1.bgra[0]) / 9;
					g = (0 + 0 + 0 + 0 + pij.bgra[1] + pi1j.bgra[1] + 0 + pij1.bgra[1] + pi1j1.bgra[1]) / 9;
					r = (0 + 0 + 0 + 0 + pij.bgra[2] + pi1j.bgra[2] + 0 + pij1.bgra[2] + pi1j1.bgra[2]) / 9;
				} else {
					const Pixel& pi1jm1 = in_rows[i+1][j-1];
					const Pixel& pijm1 = in_rows[i][j-1];
					if (j == width - 1) {          /* first row, last column */
						b = (0 + 0 + 0 + pijm1.bgra[0] + pij.bgra[0] + 0 + pi1jm1.bgra[0] + pi1j.bgra[0] + 0) / 9;
						g = (0 + 0 + 0 + pijm1.bgra[1] + pij.bgra[1] + 0 + pi1jm1.bgra[1] + pi1j.bgra[1] + 0) / 9;
						r = (0 + 0 + 0 + pijm1.bgra[2] + pij.bgra[2] + 0 + pi1jm1.bgra[2] + pi1j.bgra[2] + 0) / 9;
					} 
					else {                          /* first row, middle columns */
						b = (0 + 0 + 0 + pijm1.bgra[0] + pij.bgra[0] + pij1.bgra[0] + pi1jm1.bgra[0] + pi1j.bgra[0] + pi1j1.bgra[0]) / 9;
						g = (0 + 0 + 0 + pijm1.bgra[1] + pij.bgra[1] + pij1.bgra[1] + pi1jm1.bgra[1] + pi1j.bgra[1] + pi1j1.bgra[1]) / 9;
						r = (0 + 0 + 0 + pijm1.bgra[2] + pij.bgra[2] + pij1.bgra[2] + pi1jm1.bgra[2] + pi1j.bgra[2] + pi1j1.bgra[2]) / 9;
					}
				}
				
			} else if (i == height - 1) {        /* last row */
				const Pixel& pim1j = in_rows[i-1][j];
				const Pixel& pm1j1 = in_rows[i-1][j+1];
				if (j == 0) {             /* last row, first column */
					b = (0 + pim1j.bgra[0] + pm1j1.bgra[0] + 0 + pij.bgra[0] + pij1.bgra[0] + 0 + 0 + 0) / 9;
					g = (0 + pim1j.bgra[1] + pm1j1.bgra[1] + 0 + pij.bgra[1] + pij1.bgra[1] + 0 + 0 + 0) / 9;
					r = (0 + pim1j.bgra[2] + pm1j1.bgra[2] + 0 + pij.bgra[2] + pij1.bgra[2] + 0 + 0 + 0) / 9;
				} else {
					const Pixel& pijm1 = in_rows[i][j-1];
					const Pixel& pim1jm1 = in_rows[i-1][j-1];
					if (j == width - 1) {      /* last row, last column */
						b = (pim1jm1.bgra[0] + pm1j1.bgra[0] + 0 + in_rows[i][i-1].bgra[0] + pij.bgra[0] + 0 + 0 + 0 + 0) / 9;
						g = (pim1jm1.bgra[1] + pm1j1.bgra[1] + 0 + pijm1.bgra[1] + pij.bgra[1] + 0 + 0 + 0 + 0) / 9;
						r = (pim1jm1.bgra[2] + pm1j1.bgra[2] + 0 + pijm1.bgra[2] + pij.bgra[2] + 0 + 0 + 0 + 0) / 9;
					} 
					else {                          /* last row, middle columns */
						b = (pim1jm1.bgra[0] + pim1j.bgra[0] + pm1j1.bgra[0] + pijm1.bgra[0] + pij.bgra[0] + pij1.bgra[0] + 0 + 0 + 0) / 9;
						g = (pim1jm1.bgra[1] + pim1j.bgra[1] + pm1j1.bgra[1] + pijm1.bgra[1] + pij.bgra[1] + pij1.bgra[1] + 0 + 0 + 0) / 9;
						r = (pim1jm1.bgra[2] + pim1j.bgra[2] + pm1j1.bgra[2] + pijm1.bgra[2] + pij.bgra[2] + pij1.bgra[2] + 0 + 0 + 0) / 9;
					}
				}
			} else {          
				const Pixel& pim1j = in_rows[i-1][j];                  /* middle rows */
				const Pixel& pijm1 = in_rows[i][j-1];
				const Pixel& pim1jm1 = in_rows[i-1][j-1];
				const Pixel& pm1j1 = in_rows[i-1][j+1];
				if (j == 0) {                 /* middle row, first column */
					b = ( 0 + pim1j.bgra[0] + pm1j1.bgra[0] + 0 + pij.bgra[0] + pij1.bgra[0] + 0 + pi1j.bgra[0] + pi1j1.bgra[0]) / 9;
					g = ( 0 + pim1j.bgra[1] + pm1j1.bgra[1] + 0 + pij.bgra[1] + pij1.bgra[1] + 0 + pi1j.bgra[1] + pi1j1.bgra[1]) / 9;
					r = ( 0 + pim1j.bgra[2] + pm1j1.bgra[2] + 0 + pij.bgra[2] + pij1.bgra[2] + 0 + pi1j.bgra[2] + pi1j1.bgra[2]) / 9;
				} else {
					if (j == width - 1) {      /* middle row, last column */
						b = ( pim1jm1.bgra[0] + pim1j.bgra[0] + 0 + pijm1.bgra[0] + pij.bgra[0] + 0 + in_rows[i+1][j-1].bgra[0]+ pi1j.bgra[0] + 0) / 9;
						g = ( pim1jm1.bgra[1] + pim1j.bgra[1] + 0 + pijm1.bgra[1] + pij.bgra[1] + 0 + in_rows[i+1][j-1].bgra[1] + pi1j.bgra[1] + 0) / 9;
						r = ( pim1jm1.bgra[2] + pim1j.bgra[2] + 0 + pijm1.bgra[2] + pij.bgra[2] + 0 + in_rows[i+1][j-1].bgra[2] + pi1j.bgra[2] + 0) / 9;
					} 
					else {                          /* middle row, middle columns */
						b = ( pim1jm1.bgra[0] + pim1j.bgra[0] + pm1j1.bgra[0] + pijm1.bgra[0] + pij.bgra[0] + pij1.bgra[0] + in_rows[i+1][j-1].bgra[0] + pi1j.bgra[0] + pi1j1.bgra[0]) / 9;
						g = ( pim1jm1.bgra[1] + pim1j.bgra[1] + pm1j1.bgra[1] + pijm1.bgra[1] + pij.bgra[1] + pij1.bgra[1] + in_rows[i+1][j-1].bgra[1] + pi1j.bgra[1] + pi1j1.bgra[1]) / 9;
						r = ( pim1jm1.bgra[2] + pim1j.bgra[2] + pm1j1.bgra[2] + pijm1.bgra[2] + pij.bgra[2] + pij1.bgra[2] + in_rows[i+1][j-1].bgra[2] + pi1j.bgra[2] + pi1j1.bgra[2]) / 9;
					}
				}
				
			}
			Pixel& out = out_rows[i][j];
			out.bgra[0] = (b > MAX_BGR_VALUE) ? MAX_BGR_VALUE : b;
			out.bgra[1] = (g > MAX_BGR_VALUE) ? MAX_BGR_VALUE : g;
			out.bgra[2] = (r > MAX_BGR_VALUE) ? MAX_BGR_VALUE : r;
		}
	}
	return output_image_ptr;
}

// 2x1 Loop unrolling and code motion -> tried 3x1 but was slower
ImagePtr applyBlur_2(ImagePtr image_ptr) {
  	auto output_image_ptr = 
    std::make_shared<Image>(image_ptr->name() + "_blurred", IMAGE_WIDTH, IMAGE_HEIGHT);
	auto in_rows = image_ptr->rows();
	auto out_rows = output_image_ptr->rows();
	double b, g, r;
	int height = in_rows.size();
	int width = in_rows[1] - in_rows[0];
	for ( long i = 0; i < height; ++i ) {
		int limit = width - 1;
		int j;
		for (j = 0; j < limit; j += 2 ) {
			// Average = ([i-1][j-1] + [i-1][j] + [i-1][j+1] + [i][j-1] + [i][j] + [i][j+1] + [i+1][j-1] + [i+1][j] + [i+1][j+1])/ 9
			if (i == 0) {                        /* first row */
				if (j == 0) {                     /* first row, first column */
					b = (0 + 0 + 0 + 0 + in_rows[i][j].bgra[0] + in_rows[i+1][j].bgra[0] + 0 + in_rows[i][j+1].bgra[0] + in_rows[i+1][j+1].bgra[0]) / 9;
					g = (0 + 0 + 0 + 0 + in_rows[i][j].bgra[1] + in_rows[i+1][j].bgra[1] + 0 + in_rows[i][j+1].bgra[1] + in_rows[i+1][j+1].bgra[1]) / 9;
					r = (0 + 0 + 0 + 0 + in_rows[i][j].bgra[2] + in_rows[i+1][j].bgra[2] + 0 + in_rows[i][j+1].bgra[2] + in_rows[i+1][j+1].bgra[2]) / 9;
				} 
				else if (j == width - 1) {          /* first row, last column */
					b = (0 + 0 + 0 + in_rows[i][j-1].bgra[0] + in_rows[i][j].bgra[0] + 0 + in_rows[i+1][j-1].bgra[0] + in_rows[i+1][j].bgra[0] + 0) / 9;
					g = (0 + 0 + 0 + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + 0 + in_rows[i+1][j-1].bgra[1] + in_rows[i+1][j].bgra[1] + 0) / 9;
					r = (0 + 0 + 0 + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + 0 + in_rows[i+1][j-1].bgra[2] + in_rows[i+1][j].bgra[2] + 0) / 9;
				} 
				else {                          /* first row, middle columns */
					b = (0 + 0 + 0 + in_rows[i][j-1].bgra[0] + in_rows[i][j].bgra[0] + in_rows[i][j+1].bgra[0] + in_rows[i+1][j-1].bgra[0] + in_rows[i+1][j].bgra[0] + in_rows[i+1][j+1].bgra[0]) / 9;
					g = (0 + 0 + 0 + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + in_rows[i][j+1].bgra[1] + in_rows[i+1][j-1].bgra[1] + in_rows[i+1][j].bgra[1] + in_rows[i+1][j+1].bgra[1]) / 9;
					r = (0 + 0 + 0 + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + in_rows[i][j+1].bgra[2] + in_rows[i+1][j-1].bgra[2] + in_rows[i+1][j].bgra[2] + in_rows[i+1][j+1].bgra[2]) / 9;
				}
			} 
			else if (i == height - 1) {        /* last row */
				if (j == 0) {             /* last row, first column */
					b = (0 + in_rows[i-1][j].bgra[0] + in_rows[i-1][j+1].bgra[0] + 0 + in_rows[i][j].bgra[0] + in_rows[i][j+1].bgra[0] + 0 + 0 + 0) / 9;
					g = (0 + in_rows[i-1][j].bgra[1] + in_rows[i-1][j+1].bgra[1] + 0 + in_rows[i][j].bgra[1] + in_rows[i][j+1].bgra[1] + 0 + 0 + 0) / 9;
					r = (0 + in_rows[i-1][j].bgra[2] + in_rows[i-1][j+1].bgra[2] + 0 + in_rows[i][j].bgra[2] + in_rows[i][j+1].bgra[2] + 0 + 0 + 0) / 9;
				} 
				else if (j == width - 1) {      /* last row, last column */
					b = (in_rows[i-1][j-1].bgra[0] + in_rows[i-1][j+1].bgra[0] + 0 + in_rows[i][i-1].bgra[0] + in_rows[i][j].bgra[0] + 0 + 0 + 0 + 0) / 9;
					g = (in_rows[i-1][j-1].bgra[1] + in_rows[i-1][j+1].bgra[1] + 0 + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + 0 + 0 + 0 + 0) / 9;
					r = (in_rows[i-1][j-1].bgra[2] + in_rows[i-1][j+1].bgra[2] + 0 + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + 0 + 0 + 0 + 0) / 9;
				} 
				else {                          /* last row, middle columns */
					b = (in_rows[i-1][j-1].bgra[0] + in_rows[i-1][j].bgra[0] + in_rows[i-1][j+1].bgra[0] + in_rows[i][j-1].bgra[0] + in_rows[i][j].bgra[0] + in_rows[i][j+1].bgra[0] + 0 + 0 + 0) / 9;
					g = (in_rows[i-1][j-1].bgra[1] + in_rows[i-1][j].bgra[1] + in_rows[i-1][j+1].bgra[1] + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + in_rows[i][j+1].bgra[1] + 0 + 0 + 0) / 9;
					r = (in_rows[i-1][j-1].bgra[2] + in_rows[i-1][j].bgra[2] + in_rows[i-1][j+1].bgra[2] + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + in_rows[i][j+1].bgra[2] + 0 + 0 + 0) / 9;
				}
			} 
			else {                            /* middle rows */
				if (j == 0) {                 /* middle row, first column */
					b = ( 0 + in_rows[i-1][j].bgra[0] + in_rows[i-1][j+1].bgra[0] + 0 + in_rows[i][j].bgra[0] + in_rows[i][j+1].bgra[0] + 0 + in_rows[i+1][j].bgra[0] + in_rows[i+1][j+1].bgra[0]) / 9;
					g = ( 0 + in_rows[i-1][j].bgra[1] + in_rows[i-1][j+1].bgra[1] + 0 + in_rows[i][j].bgra[1] + in_rows[i][j+1].bgra[1] + 0 + in_rows[i+1][j].bgra[1] + in_rows[i+1][j+1].bgra[1]) / 9;
					r = ( 0 + in_rows[i-1][j].bgra[2] + in_rows[i-1][j+1].bgra[2] + 0 + in_rows[i][j].bgra[2] + in_rows[i][j+1].bgra[2] + 0 + in_rows[i+1][j].bgra[2] + in_rows[i+1][j+1].bgra[2]) / 9;
				} 
				else if (j == width - 1) {      /* middle row, last column */
					b = ( in_rows[i-1][j-1].bgra[0] + in_rows[i-1][j].bgra[0] + 0 + in_rows[i][j-1].bgra[0] + in_rows[i][j].bgra[0] + 0 + in_rows[i+1][j-1].bgra[0]+ in_rows[i+1][j].bgra[0] + 0) / 9;
					g = ( in_rows[i-1][j-1].bgra[1] + in_rows[i-1][j].bgra[1] + 0 + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + 0 + in_rows[i+1][j-1].bgra[1] + in_rows[i+1][j].bgra[1] + 0) / 9;
					r = ( in_rows[i-1][j-1].bgra[2] + in_rows[i-1][j].bgra[2] + 0 + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + 0 + in_rows[i+1][j-1].bgra[2] + in_rows[i+1][j].bgra[2] + 0) / 9;
				} 
				else {                          /* middle row, middle columns */
					b = ( in_rows[i-1][j-1].bgra[0] + in_rows[i-1][j].bgra[0] + in_rows[i-1][j+1].bgra[0] + in_rows[i][j-1].bgra[0] + in_rows[i][j].bgra[0] + in_rows[i][j+1].bgra[0] + in_rows[i+1][j-1].bgra[0] + in_rows[i+1][j].bgra[0] + in_rows[i+1][j+1].bgra[0]) / 9;
					g = ( in_rows[i-1][j-1].bgra[1] + in_rows[i-1][j].bgra[1] + in_rows[i-1][j+1].bgra[1] + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + in_rows[i][j+1].bgra[1] + in_rows[i+1][j-1].bgra[1] + in_rows[i+1][j].bgra[1] + in_rows[i+1][j+1].bgra[1]) / 9;
					r = ( in_rows[i-1][j-1].bgra[2] + in_rows[i-1][j].bgra[2] + in_rows[i-1][j+1].bgra[2] + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + in_rows[i][j+1].bgra[2] + in_rows[i+1][j-1].bgra[2] + in_rows[i+1][j].bgra[2] + in_rows[i+1][j+1].bgra[2]) / 9;
				}
			}
			out_rows[i][j].bgra[0] = (b > MAX_BGR_VALUE) ? MAX_BGR_VALUE : b;
			out_rows[i][j].bgra[1] = (g > MAX_BGR_VALUE) ? MAX_BGR_VALUE : g;
			out_rows[i][j].bgra[2] = (r > MAX_BGR_VALUE) ? MAX_BGR_VALUE : r;

			// #2
			// Average = ([i-1][j-1] + [i-1][j] + [i-1][j+1] + [i][j-1] + [i][j] + [i][j+1] + [i+1][j-1] + [i+1][j] + [i+1][j+1])/ 9
			if (i == 0) {                        /* first row */
				if (j == 0) {                     /* first row, first column */
					b = (0 + 0 + 0 + 0 + in_rows[i][j + 1].bgra[0] + in_rows[i+1][j + 1].bgra[0] + 0 + in_rows[i][j + 2].bgra[0] + in_rows[i+1][j + 2].bgra[0]) / 9;
					g = (0 + 0 + 0 + 0 + in_rows[i][j + 1].bgra[1] + in_rows[i+1][j + 1].bgra[1] + 0 + in_rows[i][j + 2].bgra[1] + in_rows[i+1][j + 2].bgra[1]) / 9;
					r = (0 + 0 + 0 + 0 + in_rows[i][j + 1].bgra[2] + in_rows[i+1][j + 1].bgra[2] + 0 + in_rows[i][j + 2].bgra[2] + in_rows[i+1][j + 2].bgra[2]) / 9;
				} 
				else if (j == width - 1) {          /* first row, last column */
					b = (0 + 0 + 0 + in_rows[i][j].bgra[0] + in_rows[i][j + 1].bgra[0] + 0 + in_rows[i+1][j].bgra[0] + in_rows[i+1][j + 1].bgra[0] + 0) / 9;
					g = (0 + 0 + 0 + in_rows[i][j].bgra[1] + in_rows[i][j + 1].bgra[1] + 0 + in_rows[i+1][j].bgra[1] + in_rows[i+1][j + 1].bgra[1] + 0) / 9;
					r = (0 + 0 + 0 + in_rows[i][j].bgra[2] + in_rows[i][j + 1].bgra[2] + 0 + in_rows[i+1][j].bgra[2] + in_rows[i+1][j + 1].bgra[2] + 0) / 9;
				} 
				else {                          /* first row, middle columns */
					b = (0 + 0 + 0 + in_rows[i][j].bgra[0] + in_rows[i][j + 1].bgra[0] + in_rows[i][j + 2].bgra[0] + in_rows[i+1][j].bgra[0] + in_rows[i+1][j + 1].bgra[0] + in_rows[i+1][j + 2].bgra[0]) / 9;
					g = (0 + 0 + 0 + in_rows[i][j].bgra[1] + in_rows[i][j + 1].bgra[1] + in_rows[i][j + 2].bgra[1] + in_rows[i+1][j].bgra[1] + in_rows[i+1][j + 1].bgra[1] + in_rows[i+1][j + 2].bgra[1]) / 9;
					r = (0 + 0 + 0 + in_rows[i][j].bgra[2] + in_rows[i][j + 1].bgra[2] + in_rows[i][j + 2].bgra[2] + in_rows[i+1][j].bgra[2] + in_rows[i+1][j + 1].bgra[2] + in_rows[i+1][j + 2].bgra[2]) / 9;
				}
			} 
			else if (i == height - 1) {        /* last row */
				if (j == 0) {             /* last row, first column */
					b = (0 + in_rows[i-1][j + 1].bgra[0] + in_rows[i-1][j + 2].bgra[0] + 0 + in_rows[i][j + 1].bgra[0] + in_rows[i][j + 2].bgra[0] + 0 + 0 + 0) / 9;
					g = (0 + in_rows[i-1][j + 1].bgra[1] + in_rows[i-1][j + 2].bgra[1] + 0 + in_rows[i][j + 1].bgra[1] + in_rows[i][j + 2].bgra[1] + 0 + 0 + 0) / 9;
					r = (0 + in_rows[i-1][j + 1].bgra[2] + in_rows[i-1][j + 2].bgra[2] + 0 + in_rows[i][j + 1].bgra[2] + in_rows[i][j + 2].bgra[2] + 0 + 0 + 0) / 9;
				} 
				else if (j == width - 1) {      /* last row, last column */
					b = (in_rows[i-1][j].bgra[0] + in_rows[i-1][j + 2].bgra[0] + 0 + in_rows[i][i-1].bgra[0] + in_rows[i][j + 1].bgra[0] + 0 + 0 + 0 + 0) / 9;
					g = (in_rows[i-1][j].bgra[1] + in_rows[i-1][j + 2].bgra[1] + 0 + in_rows[i][j].bgra[1] + in_rows[i][j + 1].bgra[1] + 0 + 0 + 0 + 0) / 9;
					r = (in_rows[i-1][j].bgra[2] + in_rows[i-1][j + 2].bgra[2] + 0 + in_rows[i][j].bgra[2] + in_rows[i][j + 1].bgra[2] + 0 + 0 + 0 + 0) / 9;
				} 
				else {                          /* last row, middle columns */
					b = (in_rows[i-1][j].bgra[0] + in_rows[i-1][j + 1].bgra[0] + in_rows[i-1][j + 2].bgra[0] + in_rows[i][j].bgra[0] + in_rows[i][j + 1].bgra[0] + in_rows[i][j + 2].bgra[0] + 0 + 0 + 0) / 9;
					g = (in_rows[i-1][j].bgra[1] + in_rows[i-1][j + 1].bgra[1] + in_rows[i-1][j + 2].bgra[1] + in_rows[i][j].bgra[1] + in_rows[i][j + 1].bgra[1] + in_rows[i][j + 2].bgra[1] + 0 + 0 + 0) / 9;
					r = (in_rows[i-1][j].bgra[2] + in_rows[i-1][j + 1].bgra[2] + in_rows[i-1][j + 2].bgra[2] + in_rows[i][j].bgra[2] + in_rows[i][j + 1].bgra[2] + in_rows[i][j + 2].bgra[2] + 0 + 0 + 0) / 9;
				}
			} 
			else {                            /* middle rows */
				if (j == 0) {                 /* middle row, first column */
					b = ( 0 + in_rows[i-1][j + 1].bgra[0] + in_rows[i-1][j + 2].bgra[0] + 0 + in_rows[i][j + 1].bgra[0] + in_rows[i][j + 2].bgra[0] + 0 + in_rows[i+1][j + 1].bgra[0] + in_rows[i+1][j + 2].bgra[0]) / 9;
					g = ( 0 + in_rows[i-1][j + 1].bgra[1] + in_rows[i-1][j + 2].bgra[1] + 0 + in_rows[i][j + 1].bgra[1] + in_rows[i][j + 2].bgra[1] + 0 + in_rows[i+1][j + 1].bgra[1] + in_rows[i+1][j + 2].bgra[1]) / 9;
					r = ( 0 + in_rows[i-1][j + 1].bgra[2] + in_rows[i-1][j + 2].bgra[2] + 0 + in_rows[i][j + 1].bgra[2] + in_rows[i][j + 2].bgra[2] + 0 + in_rows[i+1][j + 1].bgra[2] + in_rows[i+1][j + 2].bgra[2]) / 9;
				} 
				else if (j == width - 1) {      /* middle row, last column */
					b = ( in_rows[i-1][j].bgra[0] + in_rows[i-1][j + 1].bgra[0] + 0 + in_rows[i][j].bgra[0] + in_rows[i][j + 1].bgra[0] + 0 + in_rows[i+1][j].bgra[0]+ in_rows[i+1][j + 1].bgra[0] + 0) / 9;
					g = ( in_rows[i-1][j].bgra[1] + in_rows[i-1][j + 1].bgra[1] + 0 + in_rows[i][j].bgra[1] + in_rows[i][j + 1].bgra[1] + 0 + in_rows[i+1][j].bgra[1] + in_rows[i+1][j + 1].bgra[1] + 0) / 9;
					r = ( in_rows[i-1][j].bgra[2] + in_rows[i-1][j + 1].bgra[2] + 0 + in_rows[i][j].bgra[2] + in_rows[i][j + 1].bgra[2] + 0 + in_rows[i+1][j].bgra[2] + in_rows[i+1][j + 1].bgra[2] + 0) / 9;
				} 
				else {                          /* middle row, middle columns */
					b = ( in_rows[i-1][j].bgra[0] + in_rows[i-1][j + 1].bgra[0] + in_rows[i-1][j + 2].bgra[0] + in_rows[i][j].bgra[0] + in_rows[i][j + 1].bgra[0] + in_rows[i][j + 2].bgra[0] + in_rows[i+1][j].bgra[0] + in_rows[i+1][j + 1].bgra[0] + in_rows[i+1][j + 2].bgra[0]) / 9;
					g = ( in_rows[i-1][j].bgra[1] + in_rows[i-1][j + 1].bgra[1] + in_rows[i-1][j + 2].bgra[1] + in_rows[i][j].bgra[1] + in_rows[i][j + 1].bgra[1] + in_rows[i][j + 2].bgra[1] + in_rows[i+1][j].bgra[1] + in_rows[i+1][j + 1].bgra[1] + in_rows[i+1][j + 2].bgra[1]) / 9;
					r = ( in_rows[i-1][j].bgra[2] + in_rows[i-1][j + 1].bgra[2] + in_rows[i-1][j + 2].bgra[2] + in_rows[i][j].bgra[2] + in_rows[i][j + 1].bgra[2] + in_rows[i][j + 2].bgra[2] + in_rows[i+1][j].bgra[2] + in_rows[i+1][j + 1].bgra[2] + in_rows[i+1][j + 2].bgra[2]) / 9;
				}
			}
			out_rows[i][j + 1].bgra[0] = (b > MAX_BGR_VALUE) ? MAX_BGR_VALUE : b;
			out_rows[i][j + 1].bgra[1] = (g > MAX_BGR_VALUE) ? MAX_BGR_VALUE : g;
			out_rows[i][j + 1].bgra[2] = (r > MAX_BGR_VALUE) ? MAX_BGR_VALUE : r;
		}
		for (; j < width; j++) {
			if (i == 0) {                        /* first row */
				if (j == 0) {                     /* first row, first column */
					b = (0 + 0 + 0 + 0 + in_rows[i][j].bgra[0] + in_rows[i+1][j].bgra[0] + 0 + in_rows[i][j+1].bgra[0] + in_rows[i+1][j+1].bgra[0]) / 9;
					g = (0 + 0 + 0 + 0 + in_rows[i][j].bgra[1] + in_rows[i+1][j].bgra[1] + 0 + in_rows[i][j+1].bgra[1] + in_rows[i+1][j+1].bgra[1]) / 9;
					r = (0 + 0 + 0 + 0 + in_rows[i][j].bgra[2] + in_rows[i+1][j].bgra[2] + 0 + in_rows[i][j+1].bgra[2] + in_rows[i+1][j+1].bgra[2]) / 9;
				} 
				else if (j == width - 1) {          /* first row, last column */
					b = (0 + 0 + 0 + in_rows[i][j-1].bgra[0] + in_rows[i][j].bgra[0] + 0 + in_rows[i+1][j-1].bgra[0] + in_rows[i+1][j].bgra[0] + 0) / 9;
					g = (0 + 0 + 0 + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + 0 + in_rows[i+1][j-1].bgra[1] + in_rows[i+1][j].bgra[1] + 0) / 9;
					r = (0 + 0 + 0 + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + 0 + in_rows[i+1][j-1].bgra[2] + in_rows[i+1][j].bgra[2] + 0) / 9;
				} 
				else {                          /* first row, middle columns */
					b = (0 + 0 + 0 + in_rows[i][j-1].bgra[0] + in_rows[i][j].bgra[0] + in_rows[i][j+1].bgra[0] + in_rows[i+1][j-1].bgra[0] + in_rows[i+1][j].bgra[0] + in_rows[i+1][j+1].bgra[0]) / 9;
					g = (0 + 0 + 0 + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + in_rows[i][j+1].bgra[1] + in_rows[i+1][j-1].bgra[1] + in_rows[i+1][j].bgra[1] + in_rows[i+1][j+1].bgra[1]) / 9;
					r = (0 + 0 + 0 + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + in_rows[i][j+1].bgra[2] + in_rows[i+1][j-1].bgra[2] + in_rows[i+1][j].bgra[2] + in_rows[i+1][j+1].bgra[2]) / 9;
				}
			} 
			else if (i == height - 1) {        /* last row */
				if (j == 0) {             /* last row, first column */
					b = (0 + in_rows[i-1][j].bgra[0] + in_rows[i-1][j+1].bgra[0] + 0 + in_rows[i][j].bgra[0] + in_rows[i][j+1].bgra[0] + 0 + 0 + 0) / 9;
					g = (0 + in_rows[i-1][j].bgra[1] + in_rows[i-1][j+1].bgra[1] + 0 + in_rows[i][j].bgra[1] + in_rows[i][j+1].bgra[1] + 0 + 0 + 0) / 9;
					r = (0 + in_rows[i-1][j].bgra[2] + in_rows[i-1][j+1].bgra[2] + 0 + in_rows[i][j].bgra[2] + in_rows[i][j+1].bgra[2] + 0 + 0 + 0) / 9;
				} 
				else if (j == width - 1) {      /* last row, last column */
					b = (in_rows[i-1][j-1].bgra[0] + in_rows[i-1][j+1].bgra[0] + 0 + in_rows[i][i-1].bgra[0] + in_rows[i][j].bgra[0] + 0 + 0 + 0 + 0) / 9;
					g = (in_rows[i-1][j-1].bgra[1] + in_rows[i-1][j+1].bgra[1] + 0 + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + 0 + 0 + 0 + 0) / 9;
					r = (in_rows[i-1][j-1].bgra[2] + in_rows[i-1][j+1].bgra[2] + 0 + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + 0 + 0 + 0 + 0) / 9;
				} 
				else {                          /* last row, middle columns */
					b = (in_rows[i-1][j-1].bgra[0] + in_rows[i-1][j].bgra[0] + in_rows[i-1][j+1].bgra[0] + in_rows[i][j-1].bgra[0] + in_rows[i][j].bgra[0] + in_rows[i][j+1].bgra[0] + 0 + 0 + 0) / 9;
					g = (in_rows[i-1][j-1].bgra[1] + in_rows[i-1][j].bgra[1] + in_rows[i-1][j+1].bgra[1] + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + in_rows[i][j+1].bgra[1] + 0 + 0 + 0) / 9;
					r = (in_rows[i-1][j-1].bgra[2] + in_rows[i-1][j].bgra[2] + in_rows[i-1][j+1].bgra[2] + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + in_rows[i][j+1].bgra[2] + 0 + 0 + 0) / 9;
				}
			} 
			else {                            /* middle rows */
				if (j == 0) {                 /* middle row, first column */
					b = ( 0 + in_rows[i-1][j].bgra[0] + in_rows[i-1][j+1].bgra[0] + 0 + in_rows[i][j].bgra[0] + in_rows[i][j+1].bgra[0] + 0 + in_rows[i+1][j].bgra[0] + in_rows[i+1][j+1].bgra[0]) / 9;
					g = ( 0 + in_rows[i-1][j].bgra[1] + in_rows[i-1][j+1].bgra[1] + 0 + in_rows[i][j].bgra[1] + in_rows[i][j+1].bgra[1] + 0 + in_rows[i+1][j].bgra[1] + in_rows[i+1][j+1].bgra[1]) / 9;
					r = ( 0 + in_rows[i-1][j].bgra[2] + in_rows[i-1][j+1].bgra[2] + 0 + in_rows[i][j].bgra[2] + in_rows[i][j+1].bgra[2] + 0 + in_rows[i+1][j].bgra[2] + in_rows[i+1][j+1].bgra[2]) / 9;
				} 
				else if (j == width - 1) {      /* middle row, last column */
					b = ( in_rows[i-1][j-1].bgra[0] + in_rows[i-1][j].bgra[0] + 0 + in_rows[i][j-1].bgra[0] + in_rows[i][j].bgra[0] + 0 + in_rows[i+1][j-1].bgra[0]+ in_rows[i+1][j].bgra[0] + 0) / 9;
					g = ( in_rows[i-1][j-1].bgra[1] + in_rows[i-1][j].bgra[1] + 0 + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + 0 + in_rows[i+1][j-1].bgra[1] + in_rows[i+1][j].bgra[1] + 0) / 9;
					r = ( in_rows[i-1][j-1].bgra[2] + in_rows[i-1][j].bgra[2] + 0 + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + 0 + in_rows[i+1][j-1].bgra[2] + in_rows[i+1][j].bgra[2] + 0) / 9;
				} 
				else {                          /* middle row, middle columns */
					b = ( in_rows[i-1][j-1].bgra[0] + in_rows[i-1][j].bgra[0] + in_rows[i-1][j+1].bgra[0] + in_rows[i][j-1].bgra[0] + in_rows[i][j].bgra[0] + in_rows[i][j+1].bgra[0] + in_rows[i+1][j-1].bgra[0] + in_rows[i+1][j].bgra[0] + in_rows[i+1][j+1].bgra[0]) / 9;
					g = ( in_rows[i-1][j-1].bgra[1] + in_rows[i-1][j].bgra[1] + in_rows[i-1][j+1].bgra[1] + in_rows[i][j-1].bgra[1] + in_rows[i][j].bgra[1] + in_rows[i][j+1].bgra[1] + in_rows[i+1][j-1].bgra[1] + in_rows[i+1][j].bgra[1] + in_rows[i+1][j+1].bgra[1]) / 9;
					r = ( in_rows[i-1][j-1].bgra[2] + in_rows[i-1][j].bgra[2] + in_rows[i-1][j+1].bgra[2] + in_rows[i][j-1].bgra[2] + in_rows[i][j].bgra[2] + in_rows[i][j+1].bgra[2] + in_rows[i+1][j-1].bgra[2] + in_rows[i+1][j].bgra[2] + in_rows[i+1][j+1].bgra[2]) / 9;
				}
			}
			out_rows[i][j].bgra[0] = (b > MAX_BGR_VALUE) ? MAX_BGR_VALUE : b;
			out_rows[i][j].bgra[1] = (g > MAX_BGR_VALUE) ? MAX_BGR_VALUE : g;
			out_rows[i][j].bgra[2] = (r > MAX_BGR_VALUE) ? MAX_BGR_VALUE : r;
		}
	}
	return output_image_ptr;
}

// code motion -> removed all conditionals, reduced meory references
ImagePtr applyBlur_3(ImagePtr image_ptr) {
  	auto output_image_ptr = std::make_shared<Image>(image_ptr->name() + "_blurred", IMAGE_WIDTH, IMAGE_HEIGHT);
	auto in_rows = image_ptr->rows();
	auto out_rows = output_image_ptr->rows();
	double b, g, r;
	unsigned height = in_rows.size();
	const int width = in_rows[1] - in_rows[0];
	Pixel p1, p2, p3, p4, p5, p6, p7, p8, p9, out;

	// i = 0, j = 0
	p1 = in_rows[0][0];
	p2 = in_rows[1][0];
	p3 = in_rows[0][1];
	p4 = in_rows[1][1];
	b = (p1.bgra[0] + p2.bgra[0] + p3.bgra[0] + p4.bgra[0]) / 9;
	g = (p1.bgra[1] + p2.bgra[1] + p3.bgra[1] + p4.bgra[1]) / 9;
	r = (p1.bgra[2] + p2.bgra[2] + p3.bgra[2] + p4.bgra[2]) / 9;
	out = out_rows[0][0];
	out.bgra[0] = (b > MAX_BGR_VALUE) ? MAX_BGR_VALUE : b;
	out.bgra[1] = (g > MAX_BGR_VALUE) ? MAX_BGR_VALUE : g;
	out.bgra[2] = (r > MAX_BGR_VALUE) ? MAX_BGR_VALUE : r;


	// i = 0 j == width-1
	p1 = in_rows[0][width - 2];
	p2 = in_rows[1][width - 2];
	p3 = in_rows[0][width - 1];
	p4 = in_rows[1][width - 1];
	b = (p1.bgra[0] + p3.bgra[0] + p2.bgra[0] + p4.bgra[0]) / 9;
	g = (p1.bgra[1] + p3.bgra[1] + p2.bgra[1] + p4.bgra[1]) / 9;
	r = (p1.bgra[2] + p3.bgra[2] + p2.bgra[2] + p4.bgra[2]) / 9;
	out = out_rows[0][width - 1];
	out.bgra[0] = (b > MAX_BGR_VALUE) ? MAX_BGR_VALUE : b;
	out.bgra[1] = (g > MAX_BGR_VALUE) ? MAX_BGR_VALUE : g;
	out.bgra[2] = (r > MAX_BGR_VALUE) ? MAX_BGR_VALUE : r;

	// i = 0
	for (int i = 1; i < width - 1; i++) {
		p1 = in_rows[0][i - 1];
		p2 = in_rows[0][i];
		p3 = in_rows[0][i + 1];
		p4 = in_rows[1][i - 1];
		p5 = in_rows[2][i];
		p6 = in_rows[2][i + 1];

		b = (p1.bgra[0] + p2.bgra[0] + p3.bgra[0] + p4.bgra[0] + p5.bgra[0] + p6.bgra[0]) / 9;
		g = (p1.bgra[1] + p2.bgra[1] + p3.bgra[1] + p4.bgra[1] + p5.bgra[1] + p6.bgra[1]) / 9;
		r = (p1.bgra[2] + p2.bgra[2] + p3.bgra[2] + p4.bgra[2] + p5.bgra[2] + p6.bgra[2]) / 9;
		out = out_rows[0][i];
		out.bgra[0] = (b > MAX_BGR_VALUE) ? MAX_BGR_VALUE : b;
		out.bgra[1] = (g > MAX_BGR_VALUE) ? MAX_BGR_VALUE : g;
		out.bgra[2] = (r > MAX_BGR_VALUE) ? MAX_BGR_VALUE : r;	
	}

	// i = height - 1,  j == 0
	p1 = in_rows[height - 2][0];
	p2 = in_rows[height - 2][1];
	p3 = in_rows[height - 1][0];
	p4 = in_rows[height - 1][1];
	b = (p1.bgra[0] + p2.bgra[0] + p3.bgra[0] + p4.bgra[0]) / 9;
	g = (p1.bgra[1] + p2.bgra[1] + p3.bgra[1] + p4.bgra[1]) / 9;
	r = (p1.bgra[2] + p2.bgra[2] + p3.bgra[2] + p4.bgra[2]) / 9;
	out = out_rows[height-1][0];
	out.bgra[0] = (b > MAX_BGR_VALUE) ? MAX_BGR_VALUE : b;
	out.bgra[1] = (g > MAX_BGR_VALUE) ? MAX_BGR_VALUE : g;
	out.bgra[2] = (r > MAX_BGR_VALUE) ? MAX_BGR_VALUE : r;	

	// i = height -1, j = width - 1
	p1 = in_rows[height - 2][width - 2];
	p2 = in_rows[height - 2][width];
	p3 = in_rows[height - 1][width - 2];
	p4 = in_rows[height - 1][width - 1];
	b = (p1.bgra[0] + p2.bgra[0] + in_rows[height - 1][height-2].bgra[0] + p4.bgra[0]) / 9;
	g = (p1.bgra[1] + p2.bgra[1] + p3.bgra[1] + p4.bgra[1]) / 9;
	r = (p1.bgra[2] + p2.bgra[2] + p3.bgra[2] + p4.bgra[2]) / 9;
	out = out_rows[height - 1][width - 1];
	out.bgra[0] = (b > MAX_BGR_VALUE) ? MAX_BGR_VALUE : b;
	out.bgra[1] = (g > MAX_BGR_VALUE) ? MAX_BGR_VALUE : g;
	out.bgra[2] = (r > MAX_BGR_VALUE) ? MAX_BGR_VALUE : r;	

	// i = height - 1, j = var
	for (int i = 1; i < width - 1; i++) {
		p1 = in_rows[height - 2][i - 1];
		p2 = in_rows[height - 2][i];
		p3 = in_rows[height - 2][i + 1];
		p4 = in_rows[height - 1][i - 1];
		p5 = in_rows[height - 1][i];
		p6 = in_rows[height - 1][i + 1];

		b = (p1.bgra[0] + p2.bgra[0] + p3.bgra[0] + p4.bgra[0] + p5.bgra[0] + p6.bgra[0]) / 9;
		g = (p1.bgra[1] + p2.bgra[1] + p3.bgra[1] + p4.bgra[1] + p5.bgra[1] + p6.bgra[1]) / 9;
		r = (p1.bgra[2] + p2.bgra[2] + p3.bgra[2] + p4.bgra[2] + p5.bgra[2] + p6.bgra[2]) / 9;
		out = out_rows[height - 1][i];
		out.bgra[0] = (b > MAX_BGR_VALUE) ? MAX_BGR_VALUE : b;
		out.bgra[1] = (g > MAX_BGR_VALUE) ? MAX_BGR_VALUE : g;
		out.bgra[2] = (r > MAX_BGR_VALUE) ? MAX_BGR_VALUE : r;	
	}

	// i = var, j = 0
	for (unsigned int i = 1; i < height - 1; i++) {
		p1 = in_rows[i][0];
		p2 = in_rows[i + 1][0];
		p3 = in_rows[i][1];
		p4 = in_rows[i + 1][1];
		p5 = in_rows[i - 1][0];                  
		p6 = in_rows[i - 1][1];

		b = (p5.bgra[0] + p6.bgra[0] + p1.bgra[0] + p3.bgra[0] + p2.bgra[0] + p4.bgra[0]) / 9;
		g = (p5.bgra[1] + p6.bgra[1] + p1.bgra[1] + p3.bgra[1] + p2.bgra[1] + p4.bgra[1]) / 9;
		r = (p5.bgra[2] + p6.bgra[2] + p1.bgra[2] + p3.bgra[2] + p2.bgra[2] + p4.bgra[2]) / 9;
		out = out_rows[i][0];
		out.bgra[0] = (b > MAX_BGR_VALUE) ? MAX_BGR_VALUE : b;
		out.bgra[1] = (g > MAX_BGR_VALUE) ? MAX_BGR_VALUE : g;
		out.bgra[2] = (r > MAX_BGR_VALUE) ? MAX_BGR_VALUE : r;
	}
	// i = var, j = width - 1
	for (unsigned int i = 1; i < height - 1; i++) {
		p1 = in_rows[i- 1][width - 2];
		p2 = in_rows[i - 1][width - 1];
		p3 = in_rows[i][width - 2];
		p4 = in_rows[i][width - 1];
		p5 = in_rows[i + 1][width - 2];                  
		p6 = in_rows[i + 1][width - 1];
		b = (p1.bgra[0] + p2.bgra[0] + p3.bgra[0] + p4.bgra[0] + p5.bgra[0] + p6.bgra[0]) / 9;
		g = (p1.bgra[1] + p2.bgra[1] + p3.bgra[1] + p4.bgra[1] + p5.bgra[1] + p6.bgra[1]) / 9;
		r = (p1.bgra[2] + p2.bgra[2] + p3.bgra[2] + p4.bgra[2] + p5.bgra[2] + p6.bgra[2]) / 9;
		out = out_rows[i][width - 1];
		out.bgra[0] = (b > MAX_BGR_VALUE) ? MAX_BGR_VALUE : b;
		out.bgra[1] = (g > MAX_BGR_VALUE) ? MAX_BGR_VALUE : g;
		out.bgra[2] = (r > MAX_BGR_VALUE) ? MAX_BGR_VALUE : r;
	}

	// middle of image
	for (unsigned i = 1; i < height - 1; i++) {
		for (int j = 1; j < width - 1; j++) {
			p1 = in_rows[i][j];
			p2 = in_rows[i+1][j];
			p3 = in_rows[i][j+1];
			p4 = in_rows[i+1][j+1];
			p5 = in_rows[i-1][j];                  /* middle rows */
			p6 = in_rows[i-1][j+1];
			p7 = in_rows[i][j-1];
			p8 = in_rows[i-1][j-1];
			p9 = in_rows[i+1][j-1];
					
			b = (p8.bgra[0] + p5.bgra[0] + p6.bgra[0] + p7.bgra[0] + p1.bgra[0] + p3.bgra[0] + p9.bgra[0] + p2.bgra[0] + p4.bgra[0]) / 9;
			g = (p8.bgra[1] + p5.bgra[1] + p6.bgra[1] + p7.bgra[1] + p1.bgra[1] + p3.bgra[1] + p9.bgra[1] + p2.bgra[1] + p4.bgra[1]) / 9;
			r = (p8.bgra[2] + p5.bgra[2] + p6.bgra[2] + p7.bgra[2] + p1.bgra[2] + p3.bgra[2] + p9.bgra[2] + p2.bgra[2] + p4.bgra[2]) / 9;
			
			
			out = out_rows[i][j];
			out.bgra[0] = (b > MAX_BGR_VALUE) ? MAX_BGR_VALUE : b;
			out.bgra[1] = (g > MAX_BGR_VALUE) ? MAX_BGR_VALUE : g;
			out.bgra[2] = (r > MAX_BGR_VALUE) ? MAX_BGR_VALUE : r;
				
		}
	}
	return output_image_ptr;
}

void writeImage(ImagePtr image_ptr) {
	image_ptr->write( (image_ptr->name() + ".bmp").c_str());
}

void checkCorrectness(std::vector<ImagePtr> image_vector, std::vector<ImagePtr> image_vector_original){
	for(unsigned long k=0; k<image_vector.size(); k++) {
		ImagePtr img1 = image_vector[k];
		ImagePtr img2 = image_vector_original[k];
		auto in_rows1 = img1->rows();
		auto in_rows2 = img2->rows();
		int height1 = img1->height();
		int width1 = img1->width();
		int height2 = img2->height();
		int width2 = img2->width();
		if(height1 != height2 || width1 != width2) {
			printf("The two images do not have the same dimensions");
			return;
		}
		for(int i=0; i<height1; i++) {
			for(int j=0; j<width1; j++) {
				if(in_rows1[i][j].value != in_rows2[i][j].value) {
					printf("Correctness check failed for pixels image_%ld[%d][%d] = %d and image_refrence_%ld[%d][%d] = %d\n", (k+1), i, j, in_rows1[i][j].value, (k+1), i, i, in_rows2[i][j].value);
					return;
				}
			}
		}
		printf("Correctness check passed for image %ld\n", (k+1));
	}
}

int main() {
	const double tint_array[] = {0.75, 0, 0};
	
	// Store the output of the original functions applyGamma, applyTint, applyBlur in image_vector_reference
	// The output images will be used for checking the correctness of the optimized functions
	int applyGammeTime, applyBlurTime, applyTintTime;
	int applyGammaOG, applyBlurOG;
	clock_t start, end;
	int n = 1;
	
	// Create two vector to hold 4 fractal images
	std::vector<ImagePtr> image_vector, image_vector_reference;
	for (int i = 2000; i <= 2000000; i *= 10) {
		image_vector.push_back(makeFractalImage(i));
		image_vector_reference.push_back(makeFractalImage(i));
	}
	printf("Image\tGamma\tTint\tBlur\tTotal\n");

	for(ImagePtr img:image_vector_reference) {
		start = clock();
		img = applyGamma(img, 1.4); 
		end = clock();
		applyGammaOG = end - start;

		start = clock();
		img = applyTint(img, tint_array);
		end = clock();
		applyTintTime = end - start;

		start = clock();
		img = applyBlur(img);
		end = clock();
		applyBlurOG = end - start;
		printf("%d\t%d\t%d\t%d\t%d\t\n", n, applyGammaOG, applyTintTime, applyBlurOG, applyGammaOG + applyTintTime + applyBlurOG);
		n++;
	}
	// Process the images in the vector image_vector
	process_images(image_vector);
	// check the output images of process_images against the images processed by the original functions
	checkCorrectness(image_vector, image_vector_reference);
	
	printf("\n\nOptimization 1 -> applyGamma code motion, reduced memory references, and reduced function calls\n");
	printf("Image\tGamma\tTint\tBlur\tTotal\tSpeedup\n");
	n = 1;
	for(ImagePtr img:image_vector_reference) {
		start = clock();
		img = applyGamma_1(img, 1.4); 
		end = clock();
		applyGammeTime = end - start;

		start = clock();
		img = applyTint(img, tint_array);
		end = clock();
		applyTintTime = end - start;

		start = clock();
		img = applyBlur(img);
		end = clock();
		applyBlurTime = end - start;
		printf("%d\t%d\t%d\t%d\t%d\t%d\n", n, applyGammeTime, applyTintTime, applyBlurTime, applyGammeTime + applyTintTime + applyBlurTime, applyGammaOG - applyGammeTime );
		n++;
	}
	// Process the images in the vector image_vector
	process_images(image_vector);
	// check the output images of process_images against the images processed by the original functions
	checkCorrectness(image_vector, image_vector_reference);

	printf("\n\nOptimization 2 -> applyBlur code motion, reduced memory references, and reduced function calls\n");
	printf("Image\tGamma\tTint\tBlur\tTotal\tSpeedup\n");
	n = 1;
	for(ImagePtr img:image_vector_reference) {
		start = clock();
		img = applyGamma(img, 1.4); 
		end = clock();
		applyGammeTime = end - start;

		start = clock();
		img = applyTint(img, tint_array);
		end = clock();
		applyTintTime = end - start;

		start = clock();
		img = applyBlur_1(img);
		end = clock();
		applyBlurTime = end - start;
		printf("%d\t%d\t%d\t%d\t%d\t%d\n", n, applyGammeTime, applyTintTime, applyBlurTime, applyGammeTime + applyTintTime + applyBlurTime, applyBlurOG - applyBlurTime );
		n++;
	}
	// Process the images in the vector image_vector
	process_images(image_vector);
	// check the output images of process_images against the images processed by the original functions
	checkCorrectness(image_vector, image_vector_reference);

	printf("\n\nOptimization 3 -> applyGamma 3x3 loop unrolling and code motion\n");
	printf("Image\tGamma\tTint\tBlur\tTotal\tSpeedup\n");
	n = 1;
	for(ImagePtr img:image_vector_reference) {
		start = clock();
		img = applyGamma_2(img, 1.4); 
		end = clock();
		applyGammeTime = end - start;

		start = clock();
		img = applyTint(img, tint_array);
		end = clock();
		applyTintTime = end - start;

		start = clock();
		img = applyBlur(img);
		end = clock();
		applyBlurTime = end - start;
		printf("%d\t%d\t%d\t%d\t%d\t%d\n", n, applyGammeTime, applyTintTime, applyBlurTime, applyGammeTime + applyTintTime + applyBlurTime, applyGammaOG - applyGammeTime );
		n++;
	}
	// Process the images in the vector image_vector
	process_images(image_vector);
	// check the output images of process_images against the images processed by the original functions
	checkCorrectness(image_vector, image_vector_reference);

	printf("\n\nOptimization 4 -> applyBlur 2x1 Loop unrolling and code motion\n");
	printf("Image\tGamma\tTint\tBlur\tTotal\tSpeedup\n");
	n = 1;
	for(ImagePtr img:image_vector_reference) {
		start = clock();
		img = applyGamma(img, 1.4); 
		end = clock();
		applyGammeTime = end - start;

		start = clock();
		img = applyTint(img, tint_array);
		end = clock();
		applyTintTime = end - start;

		start = clock();
		img = applyBlur_2(img);
		end = clock();
		applyBlurTime = end - start;
				printf("%d\t%d\t%d\t%d\t%d\t%d\n", n, applyGammeTime, applyTintTime, applyBlurTime, applyGammeTime + applyTintTime + applyBlurTime, applyBlurOG - applyBlurTime );
		n++;
	}
	// Process the images in the vector image_vector
	process_images(image_vector);
	// check the output images of process_images against the images processed by the original functions
	checkCorrectness(image_vector, image_vector_reference);

	printf("\n\nOptimization 5 -> applyGamma 6x6 loop unrolling, code motion, and reduced memory references\n");
	printf("Image\tGamma\tTint\tBlur\tTotal\tSpeedup\n");
	n = 1;
	for(ImagePtr img:image_vector_reference) {
		start = clock();
		img = applyGamma_3(img, 1.4); 
		end = clock();
		applyGammeTime = end - start;

		start = clock();
		img = applyTint(img, tint_array);
		end = clock();
		applyTintTime = end - start;

		start = clock();
		img = applyBlur(img);
		end = clock();
		applyBlurTime = end - start;
		printf("%d\t%d\t%d\t%d\t%d\t%d\n", n, applyGammeTime, applyTintTime, applyBlurTime, applyGammeTime + applyTintTime + applyBlurTime, applyGammaOG - applyGammeTime );
		n++;
	}
	// Process the images in the vector image_vector
	process_images(image_vector);
	// check the output images of process_images against the images processed by the original functions
	checkCorrectness(image_vector, image_vector_reference);

	printf("\n\nOptimization 6 -> applyBlur code motion -> removed all conditionals, reduced meory references\n");
	printf("Image\tGamma\tTint\tBlur\tTotal\tSpeedup\n");
	n = 1;
	for(ImagePtr img:image_vector_reference) {
		start = clock();
		img = applyGamma(img, 1.4); 
		end = clock();
		applyGammeTime = end - start;

		start = clock();
		img = applyTint(img, tint_array);
		end = clock();
		applyTintTime = end - start;

		start = clock();
		img = applyBlur_3(img);
		end = clock();
		applyBlurTime = end - start;
		printf("%d\t%d\t%d\t%d\t%d\t%d\n", n, applyGammeTime, applyTintTime, applyBlurTime, applyGammeTime + applyTintTime + applyBlurTime, applyBlurOG - applyBlurTime );
		n++;
	}
	// Process the images in the vector image_vector
	process_images(image_vector);
	// check the output images of process_images against the images processed by the original functions
	checkCorrectness(image_vector, image_vector_reference);

	printf("\n\nMost efficient algorithms\n");
	printf("Image\tGamma\tTint\tBlur\tTotal\tSpeedup-Gamma\tSpeedup-Blur\n");
	n = 1;
	for(ImagePtr img:image_vector_reference) {
		start = clock();
		img = applyGamma_3(img, 1.4); 
		end = clock();
		applyGammeTime = end - start;

		start = clock();
		img = applyTint(img, tint_array);
		end = clock();
		applyTintTime = end - start;

		start = clock();
		img = applyBlur_3(img);
		end = clock();
		applyBlurTime = end - start;
		printf("%d\t%d\t%d\t%d\t%d\t%d\t\t%d\n", n, applyGammeTime, applyTintTime, applyBlurTime, applyGammeTime + applyTintTime + applyBlurTime, applyGammaOG - applyGammeTime, applyBlurOG - applyBlurTime);
		n++;
	}
	// Process the images in the vector image_vector
	process_images(image_vector);
	// check the output images of process_images against the images processed by the original functions
	checkCorrectness(image_vector, image_vector_reference);
	return 0;
}

