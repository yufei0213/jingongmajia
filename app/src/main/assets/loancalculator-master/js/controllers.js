angular.module('LoanCalculator', []).controller('LoanCalculatorController', 
		function($scope) {
	         $scope.loan={
					corpus:      200000,
					mrate:       5.58,
					period:      15,
					total:       0,
					rateTotal:   0,
					detailPerMs: [],
					//等额本息每月还款额
					getTotalPerM: function(){
						//var _loan;
						//_loan = $scope.loan;

						var x = this.mrate/12/100;
						var y = this.period*12;
						return this.corpus * (x*Math.pow((1+x),y))/(Math.pow((1+x),y)-1);
					},
					//还款总额
					getTotal: function(){
						return this.getTotalPerM() * this.period * 12;
					},
					//总利息
					getRateTotal: function(){
						return this.getTotal() - $scope.loan.corpus;
					},
					//等额本息获取每月明细
					getDetailPerMsByBenxi: function(){
						this.detailPerMs = [];
						for (var i = 0; i < $scope.loan.period * 12; i++) {
							this.detailPerMs[i] = makeDetailPerM(i);
						};
						this.total = this.getTotalPerM() * this.period * 12;
						this.rateTotal = this.getTotal() - $scope.loan.corpus;
					},
					//等额本金获取每月明细
					getDetailPerMsByBenjin: function(){
						this.detailPerMs = [];
						for (var i = 0; i < $scope.loan.period * 12; i++) {
							this.detailPerMs[i] = makeDetailPerMByBenjin(i);
						};
						this.total = this.detailPerMs[$scope.loan.period * 12 -1].leiji;
						this.rateTotal = this.detailPerMs[$scope.loan.period * 12 -1].lixiTotal;
					}
				};
			//等额本息每月明细对象工厂
			var makeDetailPerM = function(index){
				var detailPerM = {};
				//本月还款
				detailPerM.corpusThisM = $scope.loan.getTotalPerM();
				//当月利息
				detailPerM.lixi;
				if(index === 0){
					//上期剩余本金*月利率
					detailPerM.lixi = $scope.loan.corpus * $scope.loan.mrate / 100 / 12;
				}
				else{
					detailPerM.lixi = ($scope.loan.corpus - $scope.loan.detailPerMs[index-1].benjinTotal) * $scope.loan.mrate / 100 / 12;
				}
				//本金
				detailPerM.benjin = detailPerM.corpusThisM - detailPerM.lixi;

				//已还本金总额
				if(index  === 0){
					detailPerM.benjinTotal = detailPerM.benjin;
				}
				else{
					detailPerM.benjinTotal = $scope.loan.detailPerMs[index-1].benjinTotal + detailPerM.benjin;
				}

				//已还利息总额
				if(index === 0){
					detailPerM.lixiTotal = detailPerM.lixi;
				}
				else{
					detailPerM.lixiTotal = $scope.loan.detailPerMs[index-1].lixiTotal + detailPerM.lixi;
				}

				//累计还款
				detailPerM.leiji = detailPerM.corpusThisM * (index + 1);

				return detailPerM;
			};
			//等额本金每月明细对象工厂
			var makeDetailPerMByBenjin = function(index){
				var detailPerM = {};
				//本月本金
				detailPerM.benjin = $scope.loan.corpus / $scope.loan.period / 12;
				//已还本金总额
				if(index === 0){
					detailPerM.benjinTotal = detailPerM.benjin;
				}
				else{
					detailPerM.benjinTotal = $scope.loan.corpus / $scope.loan.period / 12 * (index + 1);
				}
				//本月利息
				if(index === 0){
					detailPerM.lixi = $scope.loan.corpus * $scope.loan.mrate / 100 / 12;
				}
				else{
					detailPerM.lixi = ($scope.loan.corpus - $scope.loan.detailPerMs[index-1].benjinTotal) * $scope.loan.mrate / 100 / 12;
				}
				//本月还款
				detailPerM.corpusThisM = detailPerM.benjin + detailPerM.lixi
				//已还利息总额
				if(index === 0){
					detailPerM.lixiTotal = detailPerM.lixi;
				}
				else{
					detailPerM.lixiTotal = $scope.loan.detailPerMs[index-1].lixiTotal + detailPerM.lixi;
				}
				//累计还款
				if(index === 0){
					detailPerM.leiji = detailPerM.benjin + detailPerM.lixi;
				}
				else{
					detailPerM.leiji = $scope.loan.detailPerMs[index-1].leiji + detailPerM.corpusThisM;
				}
				return detailPerM;
			};
	    });