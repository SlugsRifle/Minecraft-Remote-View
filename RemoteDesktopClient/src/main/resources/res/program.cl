#pragma OPENCL EXTENSION cl_khr_fp16 : enable

const sampler_t samplerIn = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_CLAMP | CLK_FILTER_NEAREST;

int getDistance(int r1, int g1, int b1, int r2, int g2, int b2) {
    int rmean = (r1 + r2) / 2;
	int r = r1 - r2; 
	int g = g1 - g2; 
	int b = b1 - b2;
	int weightR = 2 + rmean / 256;
    int weightG = 4.0;
    int weightB = 2 + (255 - rmean) / 256;
	return weightR * r * r + weightG * g * g + weightB * b * b;
}

int matchColor(int r, int g, int b, __global int *pr, __global int *pg, __global int *pb, const int size) {
	int index = 0;
	double best = -1;
	for (int i = 4; i < size; i++) {
		double distance = getDistance(r, g, b, pr[i], pg[i], pb[i]);
		if (distance < best || best == -1) {
			best = distance;
			index = i;
		}
	}
	return index;  
}

__kernel void imageToBytes(__read_only image2d_t sourceImage, __global char *buf, __global int *R, __global int *G, __global int *B, const int size) {
	int gidX = get_global_id(0);
	int gidY = get_global_id(1);
	int w = get_image_width(sourceImage);
	int2 pos = (int2)(gidX, gidY);
	uint4 pixel = read_imageui(sourceImage, samplerIn, pos);
	buf[gidY * w + gidX] = matchColor(pixel.x, pixel.y, pixel.z, R, G, B, size);
}

/*__kernel void imageToBytes(__read_only image2d_t sourceImage, __global char *buf, __global int *R, __global int *G, __global int *B, __constant int size) {
	int gidX = get_global_id(0);
	int gidY = get_global_id(1);
	int w = get_image_width(sourceImage);
	int h = get_image_height(sourceImage); 
	int index = 0;
	int best = -1;
	int2 pos = (int2)(gidX, gidY);
	uint4 pixel = read_imageui(sourceImage, samplerIn, pos);
	for (int i = 4; i < size; ++i) {
		int r = pixel.x - R[i]; 
		int g = pixel.y - G[i]; 
		int b = pixel.z - B[i]; 
		int weightR = 10; 
		int weightG = 10; 
		int weightB = 10; 
		int distance = weightR * r * r + weightG * g * g + weightB * b * b;
		if (distance < best || best == -1) {
			best = distance;
			index = i;
		}
	}
	buf[gidY * w + gidX] = index;
}*/