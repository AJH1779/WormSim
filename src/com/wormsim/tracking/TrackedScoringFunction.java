/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wormsim.tracking;

import com.wormsim.animals.ScoringFunction;

/**
 *
 * @author ah810
 */
public interface TrackedScoringFunction extends TrackedValue, ScoringFunction {
	@Override
	public TrackedScoringFunction copy();
}
