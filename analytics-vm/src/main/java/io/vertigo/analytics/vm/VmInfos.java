package io.vertigo.analytics.vm;

import java.util.Date;

import io.vertigo.dynamo.domain.model.DtObject;
import io.vertigo.dynamo.domain.stereotype.Field;

public class VmInfos implements DtObject {

	private static final long serialVersionUID = -5137544252204057439L;

	private Date timestamp;
	private Long cpu;
	private Long memory;
	private String vmName;

	@Field(domain = "DO_TIMESTAMP", required = true, label = "Id de la definition du processus")
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	@Field(domain = "DO_NUMERIC", label = "Cpu")
	public Long getCpu() {
		return cpu;
	}

	public void setCpu(final Long cpu) {
		this.cpu = cpu;
	}

	@Field(domain = "DO_NUMERIC", label = "Memory")
	public Long getMemory() {
		return memory;
	}

	public void setMemory(final Long memory) {
		this.memory = memory;
	}

	@Field(domain = "DO_STRING", label = "VmName")
	public String getVmName() {
		return vmName;
	}

	public void setVmName(final String vmName) {
		this.vmName = vmName;
	}

}
