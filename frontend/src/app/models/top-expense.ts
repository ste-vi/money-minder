import {Category} from "./category";

export interface TopExpense {
  total: number;
  category: Category;
  currencySign: string;
  percentage: number;
}
