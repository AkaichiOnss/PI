package com.example.demo.service;

import java.text.NumberFormat;
import java.text.ParseException;

import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
	
	private final NumberFormat nfPercent;
    private final NumberFormat nfCurrency;

    PaymentServiceImpl()
    {
        // establish percentage formatter.
        nfPercent = NumberFormat.getPercentInstance();
        nfPercent.setMinimumFractionDigits(2);
        nfPercent.setMaximumFractionDigits(4);

        // establish currency formatter.
        nfCurrency = NumberFormat.getCurrencyInstance();
        nfCurrency.setMinimumFractionDigits(2);
        nfCurrency.setMaximumFractionDigits(2);
    } 
    
	@Override
	public String formatCurrency(double number) {
		return nfCurrency.format(number);
	}

	@Override
	public String formatPercent(double number) {
		return nfPercent.format(number);
	}

	@Override
	public double stringToPercent(String s) throws ParseException {
		return nfPercent.parse(s).doubleValue();
	}

	@Override
	public double getMonthlyInterestRate(double interestRate) {
		return interestRate / 100 / 12;
	}

	@Override
	public double pmt(double r, int nper, double pv, double fv, int type) {
		if (r == 0) {
            return -(pv + fv) / nper;
        }

        // i.e., pmt = r / ((1 + r)^N - 1) * -(pv * (1 + r)^N + fv)
        double pmt = r / (Math.pow(1 + r, nper) - 1) * -(pv * Math.pow(1 + r, nper) + fv);

        // account for payments at beginning of period versus end.
        if (type == 1) {
            pmt /= (1 + r);
        }

        // return results to caller.
        return pmt;
	}

	@Override
	public double pmt(double r, int nper, double pv, double fv) {
		return pmt(r, nper, pv, fv, 0);
	}

	@Override
	public double pmt(double r, int nper, double pv) {
		return pmt(r, nper, pv, 0, 0);
	}

	@Override
	public double fv(double r, int nper, double c, double pv, int type) {
		if (r == 0) return pv;

        // account for payments at beginning of period versus end.
        // since we are going in reverse, we multiply by 1 plus interest rate.
        if (type == 1) {
            c *= (1 + r);
        }

        // fv = -(((1 + r)^N - 1) / r * c + pv * (1 + r)^N);
        double fv = -((Math.pow(1 + r, nper) - 1) / r * c + pv * Math.pow(1 + r, nper));

        // return results to caller.
        return fv;
	}

	@Override
	public double fv(double r, int nper, double c, double pv) {
		 return fv(r, nper, c, pv, 0);
	}

	@Override
	public double ipmt(double r, int per, int nper, double pv, double fv, int type) {

        // Prior period (i.e., per-1) balance times periodic interest rate.
        // i.e., ipmt = fv(r, per-1, c, pv, type) * r
        // where c = pmt(r, nper, pv, fv, type)
        double ipmt = fv(r, per - 1, pmt(r, nper, pv, fv, type), pv, type) * r;

        // account for payments at beginning of period versus end.
        if (type == 1) {
            ipmt /= (1 + r);
        }

        // return results to caller.
        return ipmt;
	}

	@Override
	public double ppmt(double r, int per, int nper, double pv, double fv, int type) {
		// Calculated payment per period minus interest portion of that period.
        // i.e., ppmt = c - i
        // where c = pmt(r, nper, pv, fv, type)
        // and i = ipmt(r, per, nper, pv, fv, type)
        return pmt(r, nper, pv, fv, type) - ipmt(r, per, nper, pv, fv, type);
	}

}
