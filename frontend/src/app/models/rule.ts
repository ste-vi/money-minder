import {Category} from "./category";

export interface Rule {
  id: string;
  assignCategory: Category;
  condition: Condition;
}

export interface Condition {
  id: string;
  textToApply: string;
  type: ConditionType;
}

export interface ConditionType {
  value: string,
  description: string
}

export enum ConditionTypeEnum {
  TEXT_CONTAINS = "TEXT_CONTAINS",
  TEXT_EQUALS = "TEXT_EQUALS",
}
