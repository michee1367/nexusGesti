import * as React from "react";
import { cn } from "@/lib/utils";

interface CheckboxProps extends Omit<React.ButtonHTMLAttributes<HTMLButtonElement>, "onChange"> {
  checked?: boolean;
  onChange?: (checked: boolean) => void;
}

const Checkbox = React.forwardRef<HTMLButtonElement, CheckboxProps>(
  ({ className, checked = false, onChange, ...props }, ref) => (
    <button
      type="button"
      ref={ref}
      role="checkbox"
      aria-checked={checked}
      onClick={(event) => {
        onChange?.(!checked);
        props.onClick?.(event);
      }}
      className={cn(
        "flex h-4 w-4 shrink-0 items-center justify-center rounded border transition-colors",
        checked ? "border-primary bg-primary text-white" : "border-slate-300 bg-white",
        className
      )}
      {...props}
    >
      {checked && (
        <svg viewBox="0 0 12 12" className="h-3 w-3" fill="none">
          <path
            d="M2.5 6.2L5 8.5l4.5-5"
            stroke="currentColor"
            strokeWidth="1.6"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </svg>
      )}
    </button>
  )
);
Checkbox.displayName = "Checkbox";

export { Checkbox };