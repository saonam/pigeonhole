package com.huijimuhei.beacon.algorithm;

import android.support.annotation.Nullable;

import com.huijimuhei.beacon.data.BleDevice;

import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

public class TrilaterationHelper {

    private List<BleDevice> beacons;
    private SquaresDimension dimension;
    private double[][] positions;
    private double[] distances;

    public TrilaterationHelper() {
        dimension = SquaresDimension.TwoDimensional;
    }

    public boolean trilaterationIsPossible(List<BleDevice> beacons) {
        this.beacons = beacons;
        if (dimension != null && beacons != null
                && ((beacons.size() >= 3 && dimension == SquaresDimension.TwoDimensional)
                || (beacons.size() >= 4 && dimension == SquaresDimension.ThreeDimensional))) {
            initializeMatrices();
            return true;
        }
        return false;
    }

    /**
     * @return optimum of trilateration function (containing coordinates extra information like
     * and standard deviation). This method may fail in some cases and return null.
     */
    @Nullable
    public LeastSquaresOptimizer.Optimum getOptimum() {
        if (beacons != null) {
            return solveTrilaterationProblem();
        }
        return null;
    }

    /**
     * Call this for testing only - this tries to find a linear solution which may fail in many cases
     *
     * @return linear solution
     */
    public RealVector getLinearSolution() {
        if (beacons != null) {
            try {
                LinearLeastSquaresSolver solver = new LinearLeastSquaresSolver(
                        new TrilaterationFunction(positions, distances));
                return solver.solve(true);
            } catch (TooManyEvaluationsException | TooManyIterationsException e) {
                // optimizer failed for input values
            }
        }
        return null;
    }

    @Nullable
    private LeastSquaresOptimizer.Optimum solveTrilaterationProblem() {
        try {
            NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(
                    new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
            return solver.solve(true);
        } catch (TooManyEvaluationsException | TooManyIterationsException e) {
            // optimizer failed for input values
        }
        return null;
    }

    private void initializeMatrices() {
        boolean useThreeDimensions = beacons.size() > 3 && dimension != null
                && dimension.equals(SquaresDimension.ThreeDimensional);
        positions = new double[beacons.size()][useThreeDimensions ? 3 : 2];
        distances = new double[beacons.size()];
        for (int i = 0; i < beacons.size(); i++) {
            BleDevice beacon = beacons.get(i);
            positions[i][0] = beacon.getLat();
            positions[i][1] = beacon.getLng();
            distances[i] = beacon.getDistance();
        }
    }
}