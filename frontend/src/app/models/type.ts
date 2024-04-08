import { Account } from './account';
import {Currency} from "./currency";

export interface Type {
  id: number;
  name: string;
  accounts: Account[];
  defaultCurrency: Currency;
}
