import { Account } from './account';
import {Currency} from "./currency";

export interface Category {
  id: number;
  name: string;
  accounts: Account[];
  defaultCurrency: Currency;
}
