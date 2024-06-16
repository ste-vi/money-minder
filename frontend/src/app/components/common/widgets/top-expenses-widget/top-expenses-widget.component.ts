import { Component, OnInit } from '@angular/core';
import { DatePipe, NgForOf } from '@angular/common';
import { NgApexchartsModule } from 'ng-apexcharts';
import { CategoryService } from '../../../../services/api/category-service';

export type ChartOptions = {
  chart: any | undefined;
  dataLabels: any | undefined;
  plotOptions: any | undefined;
  legend: any | undefined;
};

@Component({
  selector: 'app-top-expenses-widget',
  standalone: true,
  imports: [DatePipe, NgForOf, NgApexchartsModule],
  templateUrl: './top-expenses-widget.component.html',
  styleUrls: ['./top-expenses-widget.component.scss'],
})
export class TopExpensesWidgetComponent implements OnInit {
  // @ts-ignore
  protected chartOptions: Partial<ChartOptions>;

  protected readonly defaultColors: string[] = [
    '#8BC1F7',
    '#519DE9',
    '#06C',
    '#004B95',
    '#BDE2B9',
    '#7CC674',
    '#4CB140',
    '#38812F',
    '#A2D9D9',
    '#73C5C5',
    '#005F60',
    '#B2B0EA',
    '#8481DD',
    '#5752D1',
    '#3C3D99',
    '#2A265F',
    '#F9E0A2',
    '#F4C145',
    '#C58C00',
    '#F4B678',
    '#F4B678',
    '#EF9234',
    '#EC7A08',
    '#8F4700',
    '#7D1007',
  ];

  protected series: any = [];
  protected labels: any = [];
  protected colors: any = [];

  constructor(private categoryService: CategoryService) {
    const currentMonth = new Date().getMonth();
    const currentYear = new Date().getFullYear();

    const dateFrom = new Date(currentYear, currentMonth, 1);
    const dateTo = new Date(currentYear, currentMonth + 1, 0);

    this.categoryService
      .getTopExpensesByCategories(dateFrom, dateTo)
      .subscribe((topExpenses) => {
        topExpenses.forEach((topExpense) => {
          this.series.push(topExpense.total);
          this.labels.push(topExpense.categoryName + " " + topExpense.total)
          this.colors.push(
            this.defaultColors[
              Math.floor(Math.random() * this.defaultColors.length)
            ],
          );
        });
      });
  }

  ngOnInit(): void {
    this.initChartOptions();
  }

  private initChartOptions() {
    const currentMonth = new Date().toLocaleString('en-US', { month: 'long' });
    const currentYear = new Date().getFullYear();

    this.chartOptions = {
      chart: {
        height: 240,
        type: 'donut',
      },
      plotOptions: {
        pie: {
          donut: {
            size: '80%',
            labels: {
              show: true,
              name: {
                show: true,
                fontSize: '1rem',
                color: 'black',
                fontFamily: 'Nunito Sans, sans-serif',
                offsetY: 0,
                formatter: function () {
                  return `${currentMonth}, ${currentYear}`;
                },
              },
              value: {
                show: true,
                color: 'black',
                fontSize: '0.9em',
                fontFamily: 'Nunito Sans, sans-serif',
                formatter: (val: string) => {
                  return val;
                },
              },
              total: {
                show: true,
                fontSize: '0.8em',
                fontFamily: 'Nunito Sans, sans-serif',
                showAlways: false,
                formatter: (w: { globals: { seriesTotals: any[] } }) => {
                  const formatter = new Intl.NumberFormat('en-US', {
                    style: 'currency',
                    currency: 'USD',
                  });

                  const total = w.globals.seriesTotals.reduce((a: any, b: any) => {
                    return a + b;
                  }, 0);

                  return formatter.format(total);
                },
              },
            },
          },
        },
      },
      dataLabels: {
        enabled: false,
      },
      legend: {
        show: true,
        fontSize: '12px',
        fontFamily: 'Nunito Sans',
        verticalAlign: 'center',
        offsetY: -16,
        offsetX: 10,
        markers: {
          onClick: function (chart: any, seriesIndex: any, opts: any) {
            console.log('series- ' + seriesIndex + "'s marker was clicked");
          },
        },
      },
    };
  }
}
