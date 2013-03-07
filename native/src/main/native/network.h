//////////////////////////////////////////////////////////////////
//                                                              //
// network.h - native Network implementation                    //
//                                                              //
// David Tompkins - 7/19/2007                                   //
//                                                              //
// http://dt.org/                                               //
//                                                              //
// Copyright (c) 2007 by David Tompkins.                        //
//                                                              //
//////////////////////////////////////////////////////////////////
//                                                              //
// This program is free software; you can redistribute it       //
// and/or modify it under the terms of the GNU General Public   //
// License as published by the Free Software Foundation.        //
//                                                              //
// This program is distributed in the hope that it will be      //
// useful, but WITHOUT ANY WARRANTY; without even the implied   //
// warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR      //
// PURPOSE. See the GNU General Public License for more details //
//                                                              //
// You should have received a copy of the GNU General Public    //
// License along with this program; if not, write to the Free   //
// Software Foundation, Inc., 59 Temple Place, Suite 330,       //
// Boston, MA 02111-1307 USA                                    //
//                                                              //
//////////////////////////////////////////////////////////////////

#ifndef _NETWORK_H
#define _NETWORK_H

#ifdef USE_AMD_MATH_LIB
#include <acml_mv.h>
#else
#include <math.h>
#endif

#ifdef __cplusplus
extern "C" {
#endif

#define MAX_NETWORKS 10
#define NO_NETWORK_AVAILABLE -1

#define DEFAULT_ETA 0.2

#ifdef USE_AMD_MATH_LIB
#define SIGMOID(x) (1.0 / (1.0 + fastexp(-x)))
#else
#define SIGMOID(x) (1.0 / (1.0 + exp(-x)))
#endif

typedef struct
{
  int inputDimension;
  int hiddenDimension;
  int outputDimension;
  double learningRate;
  double *hiddenLayer;
  double *outputLayer;
} Network;

#ifdef __cplusplus
}
#endif
#endif
